package com.nabto.androidjcenterdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nabto.api.NabtoAndroidAssetManager;
import com.nabto.api.NabtoApi;
import com.nabto.api.NabtoStatus;
import com.nabto.api.NabtoTunnelState;
import com.nabto.api.RpcResult;
import com.nabto.api.Session;
import com.nabto.api.Tunnel;
import com.nabto.api.TunnelInfoResult;

public class MainActivity extends AppCompatActivity {

    private NabtoApi api;
    private Session session;

    static final String INTERFACE_XML =
            "<unabto_queries>" +
            "  <query name='wind_speed.json' id='2'>" +
            "    <request></request>" +
            "    <response format='json'>" +
            "      <parameter name='rpc_speed_m_s_abcd' type='uint32'/>" +
            "    </response>" +
            "  </query>" +
            "</unabto_queries>";
    static final private String certUser = "johndoe";
    static final private String certPassword = "notsosecret";
    static final private String rpcUrl = "nabto://demo.nabto.net/wind_speed.json?";
    static final private String tunnelHost = "www.google.com";

    private void rpc() {
    }

    private void appendText(String text) {
        ((TextView) findViewById(R.id.textView)).append("\n" + text + "\n");
    }

    private void startProgress() {
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
    }

    private void stopProgress() {
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
    }

    private void init() {
        api = new NabtoApi(new NabtoAndroidAssetManager(this));

        ((TextView) findViewById(R.id.textView)).setText("");

        NabtoStatus status = api.startup();
        if (status != NabtoStatus.OK) {
            throw new RuntimeException("Nabto startup failed with status " + status);
        }

        status = api.createSelfSignedProfile(certUser, certPassword);
        if (status != NabtoStatus.OK) {
            throw new RuntimeException("Nabto createSelfSignedProfile failed with status " + status);
        }

        session = api.openSession(certUser, certPassword);
        if (session.getStatus() != NabtoStatus.OK) {
            throw new RuntimeException("Nabto open session failed with status " + session.getStatus());
        }

        appendText("Nabto client SDK successfully initialized, version " + api.versionString());
    }

    private void stop() {
        NabtoStatus status = api.closeSession(session);
        appendText("Close session returned " + status);
    }

    private void demoRpc() {
        RpcResult result = api.rpcSetDefaultInterface(INTERFACE_XML, session);
        if (result.getStatus() == NabtoStatus.OK) {
            appendText("Invoking RPC URL ...");
            startProgress();
            new RpcTask().execute();
        } else {
            if (result.getStatus() == NabtoStatus.FAILED_WITH_JSON_MESSAGE) {
                appendText("Nabto set RPC default interface failed: " + result.getJson());
            } else {
                appendText("Nabto set RPC default interface failed with status " + result.getStatus());
            }
        }
    }

    private void demoTunnel() {
        appendText("Opening tunnel ...");
        startProgress();
        new TunnelTask().execute();
    }

    private class TunnelTask extends AsyncTask<Void, Void, TunnelInfoResult> {
        protected void onPostExecute(TunnelInfoResult result) {
            stopProgress();
            if (result.getStatus() == NabtoStatus.OK) {
                appendText("Nabto tunnel open attempt completed, state is [" + result.getTunnelState() + "]");
                if (result.getTunnelState().equals(NabtoTunnelState.REMOTE_P2P) ||
                        result.getTunnelState().equals(NabtoTunnelState.REMOTE_RELAY) ||
                        result.getTunnelState().equals(NabtoTunnelState.REMOTE_RELAY_MICRO) ||
                        result.getTunnelState().equals(NabtoTunnelState.LOCAL)) {
                    performHttpRequest(result.getPort());
                }
            } else {
                appendText("Nabto tunnel open attempt failed with status " + result.getStatus());
            }
        }

        @Override
        protected TunnelInfoResult doInBackground(Void... voids) {
            Tunnel tunnel = api.tunnelOpenTcp(0, tunnelHost, "127.0.0.1", 80, session);
            return api.tunnelWait(tunnel, 100, 3000);
        }
    }

    private void performHttpRequest(int port) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://127.0.0.1:" + port + "/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appendText("Got an HTTP response through tunnel: \n" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                appendText("HTTP error: " + error);
            }
        });
        queue.add(stringRequest);
    }

    private class RpcTask extends AsyncTask<Void, Void, RpcResult> {

        protected void onPostExecute(RpcResult result) {
            stopProgress();
            if (result.getStatus() == NabtoStatus.OK) {
                appendText("Nabto invoke RPC OK: " + result.getJson());
            } else if (result.getStatus() == NabtoStatus.FAILED_WITH_JSON_MESSAGE) {
                appendText("Nabto invoke RPC failed: " + result.getJson());
            } else {
                appendText("Nabto invoke RPC failed with status " + result.getStatus());
            }
        }

        @Override
        protected RpcResult doInBackground(Void... voids) {
            return api.rpcInvoke(rpcUrl, session);
        }
    }

    public void initClicked(View view) {
        try {
            init();
        } catch (Exception e) {
            appendText("ERROR: " + e.getMessage());
        }
    }

    public void rpcClicked(View view) {
        if (this.api != null) {
            demoRpc();
        } else {
            appendText("Not initialized yet!");
        }
    }

    public void tunnelClicked(View view) {
        if (this.api != null) {
            demoTunnel();
        } else {
            appendText("Not initialized yet!");
        }
    }

    public void stopClicked(View view) {
        if (this.api != null) {
            stop();
        } else {
            appendText("Not initialized yet!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
