package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.*;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.JwtUtil;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.SpringUtil;
import com.cdwater.petmall.entity.PetShop;
import com.cdwater.petmall.entity.User;
import com.cdwater.petmall.model.auth.*;
import com.cdwater.petmall.config.properties.JwtProperties;
import com.cdwater.petmall.service.GenericAuthService;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class GenericAuthServiceImpl<T> implements GenericAuthService<T> {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest, Class<T> entityClass, long tokenExpiration) {
        try {
            //获取Mapper
            Object mapper = getMapperByEntityClass(entityClass);
            Method selectByUsername = mapper.getClass().getMethod("selectByUsername", String.class);

            //查询账号是否存在
            Object entityDB = selectByUsername.invoke(mapper, loginRequest.getUsername());
            //判断账号是否注销
            Method getIsDeleted = entityClass.getMethod("getIsDeleted");
            //账号不存在, 抛出异常
            if (entityDB == null || Objects.equals(getIsDeleted.invoke(entityDB), TextInfo.IS_DELETED)) {
                throw new BusinessException(ReturnMsg.LOGIN_FAILED);
            }

            //校验密码
            String passwordInput = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
            Method getPassword = entityClass.getMethod("getPassword");
            String password = (String) getPassword.invoke(entityDB);

            //密码错误, 抛出异常
            if (!StringUtils.equals(passwordInput, password)) {
                throw new BusinessException(ReturnMsg.LOGIN_FAILED);
            }

            //获取实体类的id和角色等信息
            Method getId = entityClass.getMethod("getId");
            Integer id = (Integer) getId.invoke(entityDB);
            Method getRole = entityClass.getMethod("getRole");
            Integer role = (Integer) getRole.invoke(entityDB);
            Method getNickname = entityClass.getMethod("getNickname");
            String nickname = (String) getNickname.invoke(entityDB);
            Method getAvatar = entityClass.getMethod("getAvatar");
            String avatar = (String) getAvatar.invoke(entityDB);

            //生成token
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", id);
            claims.put("role", role);
            String token = JwtUtil.buildJWT(jwtProperties.getSecret(), tokenExpiration, claims);

            //缓存token状态
            String tokenKey = RedisMark.TOKEN_PREFIX + token;
            redisUtil.setStr(tokenKey, RedisMark.WHITE, tokenExpiration, TimeUnit.MILLISECONDS, true);

            //缓存个人信息(除了密码)
            Object temp = entityClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(temp, entityDB);
            PropertyUtils.setProperty(temp, "password", null);

            //对于用户，不缓存余额
            if (RoleType.isUser(role)) {
                PropertyUtils.setProperty(temp, "balance", null);
            }

            String profileKey = RedisMark.PROFILE_PREFIX + token;
            redisUtil.setHash(profileKey, temp, tokenExpiration, TimeUnit.MILLISECONDS, true);

            //封装返回对象
            return LoginResponse.builder()
                    .id(id)
                    .role(role)
                    .nickname(nickname)
                    .avatar(avatar)
                    .token(token)
                    .build();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
    }

    @Override
    public LoginResponse phoneLogin(LoginRequest loginRequest, Class<T> entityClass, long tokenExpiration) {
        try {
            //获取正确的验证码
            String codeKey = RedisMark.CODE_PREFIX + loginRequest.getPhone();
            String cacheCode = redisUtil.getStr(codeKey);

            //校验验证码
            if (!StringUtils.equals(cacheCode, loginRequest.getCode())) {
                //校验未通过
                throw new BusinessException(ReturnMsg.CODE_ERROR);
            }

            //及时删除redis中的验证码，防止重复使用
            redisUtil.delete(codeKey);

            //获取Mapper
            Object mapper = getMapperByEntityClass(entityClass);
            Method selectByPhone = mapper.getClass().getMethod("selectByPhone", String.class);

            //校验通过，根据手机号查询用户信息
            Object entityDB = selectByPhone.invoke(mapper, loginRequest.getPhone());
            //判断账号是否注销
            Method getIsDeleted = entityClass.getMethod("getIsDeleted");

            //手机号未注册, 抛出异常
            if (entityDB == null || Objects.equals(getIsDeleted.invoke(entityDB), TextInfo.IS_DELETED)) {
                throw new BusinessException(ReturnMsg.PHONE_NOT_REGISTER);
            }

            //获取实体类的id和角色等信息
            Method getId = entityClass.getMethod("getId");
            Integer id = (Integer) getId.invoke(entityDB);
            Method getRole = entityClass.getMethod("getRole");
            Integer role = (Integer) getRole.invoke(entityDB);
            Method getNickname = entityClass.getMethod("getNickname");
            String nickname = (String) getNickname.invoke(entityDB);
            Method getAvatar = entityClass.getMethod("getAvatar");
            String avatar = (String) getAvatar.invoke(entityDB);

            //生成token
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", id);
            claims.put("role", role);
            String token = JwtUtil.buildJWT(jwtProperties.getSecret(), tokenExpiration, claims);

            //缓存token状态
            String tokenKey = RedisMark.TOKEN_PREFIX + token;
            redisUtil.setStr(tokenKey, RedisMark.WHITE, tokenExpiration, TimeUnit.MILLISECONDS, true);

            //缓存个人信息(除了密码)
            Object temp = entityClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(temp, entityDB);
            PropertyUtils.setProperty(temp, "password", null);

            //对于用户，不缓存余额
            if (RoleType.isUser(role)) {
                PropertyUtils.setProperty(temp, "balance", null);
            }

            String profileKey = RedisMark.PROFILE_PREFIX + token;
            redisUtil.setHash(profileKey, temp, tokenExpiration, TimeUnit.MILLISECONDS, true);

            // 封装返回对象
            return LoginResponse.builder()
                    .id(id)
                    .role(role)
                    .nickname(nickname)
                    .avatar(avatar)
                    .token(token)
                    .build();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
    }

    @Override
    public void forget(ForgetRequest forgetRequest, Class<T> entityClass) {
        try {
            //获取正确的验证码
            String codeKey = RedisMark.CODE_PREFIX + forgetRequest.getPhone();
            String cacheCode = redisUtil.getStr(codeKey);

            //校验验证码
            if (!StringUtils.equals(cacheCode, forgetRequest.getCode())) {
                //校验未通过
                throw new BusinessException(ReturnMsg.CODE_ERROR);
            }

            //及时删除redis中的验证码，防止重复使用
            redisUtil.delete(codeKey);

            //获取Mapper
            Object mapper = getMapperByEntityClass(entityClass);
            Method selectByPhone = mapper.getClass().getMethod("selectByPhone", String.class);

            //校验通过，根据手机号查询用户信息
            Object entityDB = selectByPhone.invoke(mapper, forgetRequest.getPhone());
            //判断账号是否注销
            Method getIsDeleted = entityClass.getMethod("getIsDeleted");

            //手机号未注册, 抛出异常
            if (entityDB == null || Objects.equals(getIsDeleted.invoke(entityDB), TextInfo.IS_DELETED)) {
                throw new BusinessException(ReturnMsg.PHONE_NOT_REGISTER);
            }

            //设置实体类的加密后的密码
            Method setPassword = entityClass.getMethod("setPassword", String.class);
            setPassword.invoke(entityDB, DigestUtils.md5DigestAsHex(forgetRequest.getNewPassword().getBytes()));

            //调用Mapper的updateById方法保存数据
            Method updateMethod = mapper.getClass().getMethod("updateById", entityClass);
            updateMethod.invoke(mapper, entityDB);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
    }

    @Override
    public void change(ChangeRequest changeRequest, Class<T> entityClass) {
        try {
            //获取Mapper
            Object mapper = getMapperByEntityClass(entityClass);
            Method selectById = mapper.getClass().getMethod("selectById", Integer.class);

            //校验通过，根据id查询用户信息
            Object entityDB = selectById.invoke(mapper, changeRequest.getId());

            //用户不存在, 抛出异常
            if (entityDB == null) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }

            //校验密码
            Method getPassword = entityClass.getMethod("getPassword");
            String passwordInput = DigestUtils.md5DigestAsHex(changeRequest.getOldPassword().getBytes());
            String password = (String) getPassword.invoke(entityDB);

            //密码错误, 抛出异常
            if (!StringUtils.equals(passwordInput, password)) {
                throw new BusinessException(ReturnMsg.OLD_PASSWORD_ERROR);
            }

            //设置实体类的加密后的密码
            Method setPassword = entityClass.getMethod("setPassword", String.class);
            setPassword.invoke(entityDB, DigestUtils.md5DigestAsHex(changeRequest.getNewPassword().getBytes()));

            //调用Mapper的updateById方法保存数据
            Method updateMethod = mapper.getClass().getMethod("updateById", entityClass);
            updateMethod.invoke(mapper, entityDB);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
    }

    @Override
    public void register(RegisterRequest registerRequest, Class<T> entityClass) {
        try {
            //获取Mapper
            Object mapper = getMapperByEntityClass(entityClass);
            Method selectByUsername = mapper.getClass().getMethod("selectByUsername", String.class);

            //查询账号是否存在，账号已存在, 则抛出异常
            Object entityDB = selectByUsername.invoke(mapper, registerRequest.getUsername());
            if (entityDB != null) {
                throw new BusinessException(ReturnMsg.ACCOUNT_REGISTER);
            }

            //通过反射动态构建完善对象
            Object newEntity = buildEntity(registerRequest, entityClass);

            //调用Mapper的insert方法保存数据
            Method insertMethod = mapper.getClass().getMethod("insert", entityClass);
            insertMethod.invoke(mapper, newEntity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
    }

    /**
     * 通过反射动态构建实体对象
     */
    private <T> T buildEntity(RegisterRequest registerRequest, Class<T> entityClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //获取实体类的 Builder对象
        Method builderMethod = entityClass.getMethod("builder");
        Object builder = builderMethod.invoke(null); //静态方法，传入null

        //获取Builder类的方法，并动态设置属性
        Class<?> builderClass = builder.getClass();

        //设置通用属性（假设所有实体类都有这些字段）
        invokeBuilderMethod(builderClass, builder, "avatar", registerRequest.getAvatar());
        invokeBuilderMethod(builderClass, builder, "username", registerRequest.getUsername());
        invokeBuilderMethod(builderClass, builder, "password", DigestUtils.md5DigestAsHex(registerRequest.getPassword().getBytes()));

        //设置特殊属性
        if (entityClass == User.class) {
            invokeBuilderMethod(builderClass, builder, "nickname",
                    TextInfo.DEFAULT_USER_NICKNAME_PREFIX + RandomStringUtils.randomAlphabetic(TextInfo.DEFAULT_NICKNAME_SUFFIX_LENGTH));
            invokeBuilderMethod(builderClass, builder, "role", RoleType.USER);
        } else if (entityClass == PetShop.class) {
            invokeBuilderMethod(builderClass, builder, "nickname",
                    TextInfo.DEFAULT_PETSHOP_NICKNAME_PREFIX + RandomStringUtils.randomAlphabetic(TextInfo.DEFAULT_NICKNAME_SUFFIX_LENGTH));
            invokeBuilderMethod(builderClass, builder, "role", RoleType.PETSHOP);
            invokeBuilderMethod(builderClass, builder, "auditStatus", TextInfo.WAIT_SUMMIT);
        }

        //调用build()方法生成实体对象
        Method buildMethod = builderClass.getMethod("build");
        return (T) buildMethod.invoke(builder);
    }

    /**
     * 反射调用Builder的setter 方法
     */
    private void invokeBuilderMethod(Class<?> builderClass, Object builder, String methodName, Object value)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = builderClass.getMethod(methodName, value.getClass());
        method.invoke(builder, value);
    }


    /**
     * 根据实体类获取对应的Mapper对象
     */
    private Object getMapperByEntityClass(Class<T> entityClass) {
        //利用命名规则
        String entityName = entityClass.getSimpleName();
        String mapperName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1) + "Mapper";
        return SpringUtil.getBean(mapperName);
    }
}
