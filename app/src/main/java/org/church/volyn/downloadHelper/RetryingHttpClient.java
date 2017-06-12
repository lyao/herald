package org.church.volyn.downloadHelper;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by user on 17.11.2014.
 */
public class RetryingHttpClient {

    private static final AbstractHttpClient httpClient;
    private static final HttpRequestRetryHandler retryHandler;
    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));

        HttpParams connManagerParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(connManagerParams, 5);
        ConnManagerParams.setMaxConnectionsPerRoute(connManagerParams,
                new ConnPerRouteBean(5));
        ConnManagerParams.setTimeout(connManagerParams, 15 * 1000);

        ThreadSafeClientConnManager cm =
                new ThreadSafeClientConnManager(connManagerParams,
                        schemeRegistry);

        HttpParams clientParams = new BasicHttpParams();
        HttpProtocolParams.setUserAgent(clientParams, "Feeds/1.0");
        HttpConnectionParams.setConnectionTimeout(clientParams, 15 * 1000);
        HttpConnectionParams.setSoTimeout(clientParams, 15 * 1000);

        httpClient = new DefaultHttpClient(cm, clientParams);
        retryHandler =
                new DefaultHttpRequestRetryHandler(5, false) {
                    public boolean retryRequest(IOException ex, int execCount,
                                                HttpContext context) {
                        if (!super.retryRequest(ex, execCount, context)) {
                            return false;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                        return true;
                    }
                };
        httpClient.setHttpRequestRetryHandler(retryHandler);
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }
}
