package me.cxd.service;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("loginValidator")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JwcLoginValidator implements LoginValidator {
    private static final String LOGIN_URL = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/";
    private static final String ACTION_URL = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";

    private CloseableHttpClient client;
    private CookieStore cookieStore;

    private void initHttpClient() {
        this.cookieStore = new BasicCookieStore();
        this.client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
    }

    private List<Cookie> getCookies() throws IOException {
        HttpGet get = new HttpGet(LOGIN_URL);
        client.execute(get);
        return cookieStore.getCookies();
    }

    private void setHeader(HttpPost post) {
        post.setHeader("User-Agent", "Mozilla/5.0");
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    public boolean isValidUser(long number, String password) {
        initHttpClient();
        HttpClientContext clientContext = HttpClientContext.create();
        HttpPost post = new HttpPost(ACTION_URL);
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("USERNAME", String.valueOf(number)));
        list.add(new BasicNameValuePair("PASSWORD", password));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, Consts.UTF_8);
        post.setEntity(entity);
        this.setHeader(post);
        CookieStore store = new BasicCookieStore();
        HttpResponse response;
        try {
            for (Cookie cookie : this.getCookies())
                store.addCookie(cookie);
            clientContext.setCookieStore(store);
            response = client.execute(post, clientContext);
            this.client.close();
        } catch (IOException e) {
            return false;
        }
        return response.getStatusLine().getStatusCode() == 302;
    }
}
