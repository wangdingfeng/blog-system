package com.traveling.realm;

import com.traveling.entity.SmpMenu;
import com.traveling.entity.SmpRole;
import com.traveling.entity.SmpUser;
import com.traveling.mapper.SmpMenuMapper;
import com.traveling.mapper.SmpRoleMapper;
import com.traveling.mapper.SmpUserMapper;
import com.traveling.mapper.SmpUserRoleMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义Realm
 * @author zjt
 *
 */
public class MyRealm extends AuthorizingRealm{

	@Resource
	private SmpUserMapper smpUserMapper;
	
	@Resource
	private SmpRoleMapper smpRoleMapper;

	@Resource
	private SmpUserRoleMapper smpUserRoleMapper;
	
	@Resource
	private SmpMenuMapper smpMenuMapper;

	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String userName=(String) SecurityUtils.getSubject().getPrincipal();

		//User user=userRepository.findByUserName(userName);
		//根据用户名查询出用户记录
		Example tuserExample=new Example(SmpUser.class);
		tuserExample.or().andEqualTo("userName",userName);
		SmpUser user= smpUserMapper.selectByExample(tuserExample).get(0);


		SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();

		//List<Role> roleList=roleRepository.findByUserId(user.getId());
		List<SmpRole> roleList = smpRoleMapper.selectRolesByUserId(user.getId());

		Set<String> roles=new HashSet<String>();
		if(roleList.size()>0){
			for(SmpRole role:roleList){
				roles.add(role.getName());
				//List<Tmenu> menuList=menuRepository.findByRoleId(role.getId());
				//根据角色id查询所有资源
				List<SmpMenu> menuList=smpMenuMapper.selectMenusByRoleId(role.getId());
				for(SmpMenu menu:menuList){
					info.addStringPermission(menu.getName()); // 添加权限
				}
			}
		}
		info.setRoles(roles);
		return info;
	}

	/**
	 * 权限认证
				*/
		@Override
		protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			String userName=(String)token.getPrincipal();
			//User user=userRepository.findByUserName(userName);
			Example tuserExample=new Example(SmpUser.class);
			tuserExample.or().andEqualTo("userName",userName);
			SmpUser user= smpUserMapper.selectByExample(tuserExample).get(0);
			if(user!=null){
				AuthenticationInfo authcInfo=new SimpleAuthenticationInfo(user.getUserName(),user.getPassword(),"xxx");
				return authcInfo;
			}else{
				return null;
			}
	}

}
