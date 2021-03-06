package com.nuvomed.commons;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUtil {

	/** 
     * httpClient的get请求方式 
     *  
     * @param url 
     * @param charset 
     * @return 
     * @throws Exception 
     */  
    public static String doGet(String url, String charset) throws MyException {      	
        HttpClient client = new HttpClient();  
        GetMethod method = new GetMethod(url);  
  
        if (null == url || !url.startsWith("http")) {  
            throw new MyException(1000);  
        }  
        // 设置请求的编码方式  
        if (null == charset) {  
        	charset= "utf-8";  
        }  
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset); 
        int statusCode;
		try {
			statusCode = client.executeMethod(method);		
	        if (statusCode != HttpStatus.SC_OK) {// 打印服务器返回的状态  
	        	throw new MyException(1001);
	        }  
	        // 返回响应消息  
	        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
	        // 在返回响应消息使用编码(utf-8)  
	        String response = new String(responseBody, charset);
	        return response; 
		} catch (HttpException e) {  
            // 发生致命的异常，可能是协议不对或者返回的内容有问题  
            System.out.println("Please check your provided http address!");
            throw new MyException(1000);            
        } catch (IOException e) {  
            // 发生网络异常  
        	throw new MyException(1002);
        } catch (Exception e) {  
            // 未知异常  
        	throw new MyException(1004); 
        } finally {  
            /* 6 .释放连接 */  
        	method.releaseConnection();  
        }    
         
    }  
  
    /** 
     * httpClient的get请求方式2 
     *  
     * @param url 
     * @param charset 
     * @return 
     * @throws Exception 
     */  
    public static String doGet2(String url, String charset) throws MyException {  
        /* 
         * 使用 GetMethod 来访问一个 URL 对应的网页,实现步骤: 1:生成一个 HttpClinet 对象并设置相应的参数。 
         * 2:生成一个 GetMethod 对象并设置响应的参数。 3:用 HttpClinet 生成的对象来执行 GetMethod 生成的Get 
         * 方法。 4:处理响应状态码。 5:若响应正常，处理 HTTP 响应内容。 6:释放连接。 
         */  
        /* 1 生成 HttpClinet 对象并设置参数 */  
        HttpClient httpClient = new HttpClient();  
        // 设置 Http 连接超时为5秒  
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);  
  
        /* 2 生成 GetMethod 对象并设置参数 */  
        GetMethod getMethod = new GetMethod(url);  
        // 设置 get 请求超时为 5 秒  
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);  
        // 设置请求重试处理，用的是默认的重试处理：请求三次  
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());          
        /* 3 执行 HTTP GET 请求 */  
        try {  
            int statusCode = httpClient.executeMethod(getMethod);  
            /* 4 判断访问的状态码 */  
            if (statusCode != HttpStatus.SC_OK) {
            	//HTTP请求响应状态异常
            	throw new MyException(1001);
            }
            else{	                                      
	            // 读取 HTTP 响应内容，这里简单打印网页内容  
	            byte[] responseBody = getMethod.getResponseBody();// 读取为字节数组  
	            String response = new String(responseBody, charset); 
	            return response;
    	        // 读取为 InputStream，在网页内容数据量大时候推荐使用  
	            // InputStream response = getMethod.getResponseBodyAsStream();  
            }             
        } catch (HttpException e) {  
            // 发生致命的异常，可能是协议不对或者返回的内容有问题  
            System.out.println("Please check your provided http address!");
            throw new MyException(1000);            
        } catch (IOException e) {  
            // 发生网络异常  
        	throw new MyException(1002);
        } catch (Exception e) {  
            // 未知异常  
        	throw new MyException(1004); 
        } finally {  
            /* 6 .释放连接 */  
            getMethod.releaseConnection();  
        }  
          
    }  
  
    /** 
     * 执行一个HTTP POST请求，返回请求响应的HTML 
     *  
     * @param url  请求的URL地址 
     * @param params  请求的查询参数,可以为null 
     * @param charset 字符集 
     * @param pretty 是否美化 
     * @return 返回请求响应的HTML 
     */  
    public static String doPost(String url,List<Header> headers, Map<String, Object> _params, String charset) throws MyException {  
                   
        HttpClient client = new HttpClient();  
        PostMethod method = new PostMethod(url);        
        						        
        for (Header header : headers) {
        	method.setRequestHeader(header); 
		}
        
        // 设置Http Post数据  
        if (_params != null) {  
            for (Map.Entry<String, Object> entry : _params.entrySet()) {  
                method.setParameter(entry.getKey(), String.valueOf(entry.getValue()));  
            }  
        }                              
          
        try {  
            client.executeMethod(method);  
            if (method.getStatusCode() == HttpStatus.SC_OK) {  
            	byte[] responseBody = method.getResponseBody();// 读取为字节数组  
            	String response = new String(responseBody, charset);
                return response;  
//                // 读取为 InputStream，在网页内容数据量大时候推荐使用  
//                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),  
//                        charset));  
//                String line;                  
//                while ((line = reader.readLine()) != null) {  
//                    if (pretty)  
//                        response.append(line).append(System.getProperty("line.separator"));  
//                    else  
//                        response.append(line);  
//                }  
//                reader.close();  
            }
            else{
            	//HTTP请求响应状态异常
            	throw new MyException(1001);
            }
        } catch (HttpException e) {
        	// 发生致命的异常，可能是协议不对或者返回的内容有问题  
        	throw new MyException(1000);       
        } catch (IOException e) { 
        	// 发生网络异常  
            throw new MyException(1002);  
        } catch (Exception e) {  
            // 未知异常  
        	throw new MyException(1004); 
        } finally {  
            method.releaseConnection();  
        }           
       
    }  
}
