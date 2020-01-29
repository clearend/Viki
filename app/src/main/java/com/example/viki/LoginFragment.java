package com.example.viki;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    String TAG = MainActivity.class.getCanonicalName();
    private EditText account, password;
    private HashMap<String, String> stringHashMap;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button1, button2;
        button1 = getView().findViewById(R.id.button4);
        button2 = getView().findViewById(R.id.button5);
        account = getView().findViewById(R.id.editText3);
        password = getView().findViewById(R.id.editText4);
        stringHashMap = new HashMap<>();

        /**
         * 按注册按钮跳转至注册页面
         */
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_loginFragment_to_registerFragment);

            }
        });


    /**
     * 点击登录按钮，跳转至首页面
     */
        button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Intent intent = new Intent(getActivity(),MainPageActivity.class);
            final Toast toast = Toast.makeText(getActivity(),"Toast",Toast.LENGTH_SHORT);
            stringHashMap.put("userId",account.getText().toString());
            stringHashMap.put("password",password.getText().toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String baseUrl = "http://192.168.0.4:8081/MedicalSystem/user/login";
                        StringBuilder tempParams = new StringBuilder();
                        int pos = 0;
                        for(String key : stringHashMap.keySet()){
                            if(pos>0){
                                tempParams.append("&");
                            }
                            tempParams.append(String.format("%s=%s",key, URLEncoder.encode(stringHashMap.get(key),"utf-8")));
                            pos ++ ;
                        }
                        String params = tempParams.toString();
                        Log.e(TAG,"params--post->>"+params);

                        // 新建一个URL对象
                        URL url = new URL(baseUrl);
                        // 打开一个HttpURLConnection连接
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        // 设置连接超时时间
                        urlConn.setConnectTimeout(5 * 1000);
                        //设置从主机读取数据超时
                        urlConn.setReadTimeout(5 * 1000);
                        // Post请求必须设置允许输出 默认false
                        urlConn.setDoOutput(true);
                        //设置请求允许输入 默认是true
                        urlConn.setDoInput(true);
                        // Post请求不能使用缓存
                        urlConn.setUseCaches(false);
                        // 设置为Post请求
                        urlConn.setRequestMethod("POST");
                        //设置本次连接是否自动处理重定向
                        urlConn.setInstanceFollowRedirects(true);
                        //配置请求Content-Type
//            urlConn.setRequestProperty("Content-Type", "application/json");//post请求不能设置这个
                        // 开始连接
                        urlConn.connect();

                        // 发送请求参数
                        PrintWriter dos = new PrintWriter(urlConn.getOutputStream());
                        dos.write(params);
                        dos.flush();
                        dos.close();
                        // 判断请求是否成功
                        if (urlConn.getResponseCode() == 200) {
                            // 获取返回的数据
                            String result = streamToString(urlConn.getInputStream());
                            Log.e(TAG, "Post方式请求成功，result--->" + result);
                            // textView.setText(result);
                            JSONObject jsonObject = new JSONObject(result);
                            Log.e(TAG,jsonObject.toString());
                            Log.e(TAG,jsonObject.get("success").toString());
                            if(jsonObject.get("success").toString().equals("true")){
                                startActivity(intent);
                            }
                            else {
                                toast.show();
                            }
                        } else {
                            Log.e(TAG, "Post方式请求失败");
                        }
                        // 关闭连接
                        urlConn.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();



        }
    });
}

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
