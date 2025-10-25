package edu.sv.ues.mv12013.splashparcial2.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.sv.ues.mv12013.splashparcial2.R;

public class MapFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;

    // HTML con el iframe provisto (Western Multidisciplinary Faculty UES), adaptado a 100% ancho/alto.
    private static final String MAPS_EMBED_HTML =
            "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "  <style>"
                    + "    html, body { margin:0; padding:0; height:100%; }"
                    + "    .map { position:fixed; inset:0; border:0; width:100%; height:100%; }"
                    + "  </style>"
                    + "</head>"
                    + "<body>"
                    + "  <iframe class=\"map\" "
                    + "    src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d6788.953477769985!2d-89.57711980917539!3d13.970575111585033!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x8f62e8f193eda62f%3A0xd40e3f2801fddb61!2sWestern%20Multidisciplinary%20Faculty%20UES!5e0!3m2!1sen!2ssv!4v1761400789228!5m2!1sen!2ssv\" "
                    + "    allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>"
                    + "</body>"
                    + "</html>";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        webView = view.findViewById(R.id.webViewMap);
        progressBar = view.findViewById(R.id.progressBarMap);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        // Mostrar progress al iniciar
        showLoading(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                showLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                showLoading(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                showLoading(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.webview_generic_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!TextUtils.isEmpty(MAPS_EMBED_HTML)) {
            webView.loadDataWithBaseURL(
                    "https://www.google.com",
                    MAPS_EMBED_HTML,
                    "text/html",
                    "UTF-8",
                    null
            );
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        // Opcional: deshabilitar interacción mientras carga
        if (webView != null) {
            webView.setAlpha(show ? 0.98f : 1f); // leve atenuación mientras carga
        }
    }

    @Override
    public void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        progressBar = null;
        super.onDestroyView();
    }
}