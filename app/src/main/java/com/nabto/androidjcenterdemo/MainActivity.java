package com.nabto.androidjcenterdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.nabto.api.*;

public class MainActivity extends AppCompatActivity {

    static final String INTERFACE_XML =
            "<unabto_queries>" +
            "  <query name='wind_speed.json' id='2'>" +
            "    <request></request>" +
            "    <response format='json'>" +
            "      <parameter name='rpc_speed_m_s' type='uint32'/>" +
            "    </response>" +
            "  </query>" +
            "</unabto_queries>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NabtoApi api = new NabtoApi(new NabtoAndroidAssetManager(this));
        api.startup();

        Session session = api.openSession("guest", "");
        if (session.getStatus() == NabtoStatus.OK) {

            RpcResult result = api.rpcSetDefaultInterface(INTERFACE_XML, session);
            if(result.getStatus() != NabtoStatus.OK) {
                // handle error
            }

            String version = api.versionString();
            result = api.rpcInvoke("nabto://demo.nabto.net/wind_speed.json?", session);
            if (result.getStatus() == NabtoStatus.OK) {
                Log.v("demo", result.getJson());
                ((TextView) findViewById(R.id.textView)).setText(version + ":\n" + result.getJson());
            }

            api.closeSession(session);
        }

        api.shutdown();
    }
}
