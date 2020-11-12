package com.sam.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties({SecurityProperties.class})
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityProperties securityProperties;

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
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String finalSecret = "{bcrypt}" + new BCryptPasswordEncoder().encode("123456");
        InMemoryClientDetailsServiceBuilder clientDetailsServiceBuilder = clients.inMemory();

        clientDetailsServiceBuilder.withClient("client_1")
                .secret(finalSecret)
                .resourceIds("order")
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("select")
                .authorities("oauth2")

                .and()

                .withClient("client_2")
                .secret(finalSecret)
                .resourceIds("order")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("server")
                .authorities("oauth2");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //指定token存储位置
                .tokenStore(tokenStore())
                // 配置JwtAccessToken转换器
                .tokenEnhancer(accessTokenConverter())
                //指定认证管理器
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        // 配置tokenServices参数
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        // 是否支持刷新
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        // 1分钟
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(20));
        endpoints.tokenServices(tokenServices);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 允许客户表单认证,不加的话/oauth/token无法访问
                .allowFormAuthenticationForClients()
                // 对于/oauth/token端点允许所有客户端发送器请求而不会被Spring-security拦截
                // 开启/oauth/token_key验证端口无权限访问
                .tokenKeyAccess("permitAll()")
                // 要访问/oauth/check_token必须设置为permitAll()，但这样所有人都可以访问了
                // 设为isAuthenticated()又导致访问不了，这个问题暂时没找到解决方案
                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("permitAll()");
    }

}

