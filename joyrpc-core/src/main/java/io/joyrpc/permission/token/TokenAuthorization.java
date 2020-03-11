package io.joyrpc.permission.token;

import io.joyrpc.InvokerAware;
import io.joyrpc.extension.Extension;
import io.joyrpc.extension.URL;
import io.joyrpc.extension.URLOption;
import io.joyrpc.permission.Authorization;
import io.joyrpc.protocol.message.Invocation;
import io.joyrpc.protocol.message.RequestMessage;
import io.joyrpc.util.GenericMethodOption;

import java.util.Optional;

import static io.joyrpc.constants.Constants.HIDDEN_KEY_TOKEN;
import static io.joyrpc.constants.Constants.METHOD_KEY;

/**
 * 基于令牌的方法权限认证
 */
@Extension(value = "token")
public class TokenAuthorization implements Authorization, InvokerAware {

    /**
     * URL
     */
    protected URL url;
    /**
     * 接口类，在泛型调用情况下，clazz和clazzName可能不相同
     */
    protected Class clazz;
    /**
     * 接口类名
     */
    protected String className;
    /**
     * 缓存令牌
     */
    protected GenericMethodOption<Optional<String>> tokens;

    @Override
    public boolean authenticate(final RequestMessage<Invocation> request) {
        //方法鉴权
        Invocation invocation = request.getPayLoad();
        String token = tokens.get(invocation.getMethodName()).orElse("");
        return token.isEmpty() || token.equals(invocation.getAttachment(HIDDEN_KEY_TOKEN));
    }

    @Override
    public void setUrl(final URL url) {
        this.url = url;
    }

    @Override
    public void setClass(final Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setClassName(final String className) {
        this.className = className;
    }

    @Override
    public void setup() {
        final String defToken = url.getString(HIDDEN_KEY_TOKEN);
        tokens = new GenericMethodOption<>(clazz, className, methodName -> Optional.ofNullable(
                url.getString(new URLOption<>(
                        METHOD_KEY.apply(methodName, HIDDEN_KEY_TOKEN), defToken))));


    }
}