package com.example.turbo.dcidr.httpclient;

import android.content.Context;

import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.global.DcidrConfig.HttpAsyncConfig;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by Turbo on 2/7/2016.
 */
public class BaseAsyncHttpClient extends AsyncHttpClient {
    //protected final String HTTP_BASE_URL = "http://ec2-52-89-146-59.us-west-2.compute.amazonaws.com";
    protected String RELATIVE_USER_URL = "user";
    protected String RELATIVE_EVENT_URL = "event";
    protected String RELATIVE_HISTORY_URL = "event";
    protected ContentType mContentType = ContentType.JSON;
    protected AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    public Context mAppContext;

    public enum ContentType {
        JSON, XML, TEXT
    }

    public void setContentType(ContentType contentType){
        mContentType = contentType;
    }

    public String getContentTypeAsString(ContentType contentType){
        if(contentType == ContentType.JSON){
            return "application/json";
        }else if(contentType == ContentType.TEXT){
            return "application/x-www-form-url";
        }else if(contentType == ContentType.XML){
            return "application/xml";
        }
        return "application/json";
    }


    public BaseAsyncHttpClient(Context context)  {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(context.getAssets().open("dcidr-public-cert.pem"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }
            //Create KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            MySSLSocketFactory mySSLSocketFactory = new MySSLSocketFactory(keyStore);
            mySSLSocketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            mAsyncHttpClient.setTimeout(30000);
            mAsyncHttpClient.setSSLSocketFactory(mySSLSocketFactory);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mAppContext = context;
        setMaxRetriesAndTimeout(HttpAsyncConfig.maxRetries, HttpAsyncConfig.retrySleep);
    }
    public void dcidrGet(String relativeUrl, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.addHeader("deviceId", DcidrApplication.getInstance().getUserCache().get("deviceId"));
        mAsyncHttpClient.addHeader("authToken", DcidrApplication.getInstance().getUserCache().get("authToken"));
        mAsyncHttpClient.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void dcidrPost(String relativeUrl, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        mAsyncHttpClient.addHeader("authToken", DcidrApplication.getInstance().getUserCache().get("authToken"));
        mAsyncHttpClient.addHeader("deviceId", DcidrApplication.getInstance().getUserCache().get("deviceId"));
        mAsyncHttpClient.post(mAppContext, getAbsoluteUrl(relativeUrl), entity, contentType, responseHandler);
    }

    public void dcidrPut(String relativeUrl, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        mAsyncHttpClient.addHeader("authToken", DcidrApplication.getInstance().getUserCache().get("authToken"));
        mAsyncHttpClient.addHeader("deviceId", DcidrApplication.getInstance().getUserCache().get("deviceId"));
        mAsyncHttpClient.put(mAppContext, getAbsoluteUrl(relativeUrl), entity, contentType, responseHandler);
    }

    public void dcidrPost(String relativeUrl, ResponseHandlerInterface responseHandler) {
        mAsyncHttpClient.addHeader("authToken", DcidrApplication.getInstance().getUserCache().get("authToken"));
        mAsyncHttpClient.addHeader("deviceId", DcidrApplication.getInstance().getUserCache().get("deviceId"));
        mAsyncHttpClient.post(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return DcidrConstant.DCIDR_SERVER_URL + DcidrConstant.BASE_URL + relativeUrl;
    }

}
