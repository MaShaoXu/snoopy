package com.sam.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }

    //"authorization_code", "password", "client_credentials", "implicit", "refresh_token"
    // 如果资源系统已经有了一套用户体系，每个用户也有了一定的权限，可以采用password模式
    // 如果仅仅是接口的对接，不考虑用户，则可以使用client模式
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String finalSecret = "{bcrypt}" + new BCryptPasswordEncoder().encode("123456");
        InMemoryClientDetailsServiceBuilder clientDetailsServiceBuilder = clients.inMemory();

        //client模式，没有用户的概念，直接与认证服务器交互，用配置中的客户端信息去申请accessToken，
        // 客户端有自己的client_id,client_secret对应于用户的username,password，而客户端也拥有自己的authorities，
        clientDetailsServiceBuilder.withClient("client_1")
                .secret(finalSecret)
                .resourceIds("order")
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("select")
                .authorities("oauth2")

                .and()

                //password模式，自己本身有一套用户体系，在认证时需要带上自己的用户名和密码，以及客户端的client_id,client_secret
                // 此时，accessToken所包含的权限是用户本身的权限，而不是客户端的权限
                .withClient("client_2")
                .secret(finalSecret)
                .resourceIds("order")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("server")
                .authorities("oauth2");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(new InMemoryTokenStore())
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 允许客户表单认证,不加的话/oauth/token无法访问
                .allowFormAuthenticationForClients()
                // 对于CheckEndpoint控制器[框架自带的校验]的/oauth/token端点允许所有客户端发送器请求而不会被Spring-security拦截
                // 开启/oauth/token_key验证端口无权限访问
                .tokenKeyAccess("permitAll()")
                // 要访问/oauth/check_token必须设置为permitAll()，但这样所有人都可以访问了，设为isAuthenticated()又导致访问不了，这个问题暂时没找到解决方案
                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("isAuthenticated()");
    }

}

