package com.lswmobile.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSError
import platform.CoreGraphics.CGRectZero
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKPreferences
import platform.WebKit.WKWebpagePreferences
import platform.WebKit.WKUIDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWindowFeatures
import platform.UIKit.UIApplication
import platform.WebKit.javaScriptEnabled
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(
    url: String,
    modifier: Modifier,
    onOpenExternal: ((String) -> Unit)?,
    onUpdateTitle: ((String?) -> Unit)?,
    onProgress: ((Float) -> Unit)?
) {
    UIKitView(
        modifier = modifier,
        factory = {
            val config = WKWebViewConfiguration()
            // Ensure JavaScript/content JS are enabled
            (config.preferences as WKPreferences).apply {
                javaScriptEnabled = true
            }
            val pagePrefs = WKWebpagePreferences().apply {
                allowsContentJavaScript = true
            }
            config.defaultWebpagePreferences = pagePrefs
            val webView = WKWebView(frame = CGRectZero.readValue(), configuration = config)
            // Use a Safari iPhone UA to encourage mobile-friendly content and broader image compatibility
            webView.customUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1"
            val navDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                    onUpdateTitle?.invoke(webView.title)
                    onProgress?.invoke(1f)
                }

                override fun webView(webView: WKWebView, didFailProvisionalNavigation: WKNavigation?, withError: NSError) {
                    println("WKWebView didFailProvisionalNavigation error: ${withError.localizedDescription}")
                }

//                override fun webView(webView: WKWebView, didFailNavigation: WKNavigation?, withError: NSError) {
//                    println("WKWebView didFailNavigation error: ${withError.localizedDescription}")
//                }

                // Allow/handle special schemes and report progress
                override fun webView(
                    webView: WKWebView,
                    decidePolicyForNavigationAction: WKNavigationAction,
                    decisionHandler: (WKNavigationActionPolicy) -> Unit
                ) {
                    val reqUrl = decidePolicyForNavigationAction.request.URL
                    val scheme = reqUrl?.scheme?.lowercase()

                    // Only treat a short allowlist of schemes as "external". Everything else
                    // (http/https/file/about/blob/data/javascript, etc.) should stay in WKWebView.
                    val externalSchemes = setOf(
                        // Keep minimal allowlist to avoid LaunchServices errors
                        "mailto", "tel", "sms", "maps"
                    )

                    if (reqUrl != null && scheme != null && externalSchemes.contains(scheme)) {
                        val absolute = reqUrl.absoluteString ?: ""
                        onOpenExternal?.invoke(absolute)
                        // Only attempt to open if the system can handle it (prevents LS errors)
                        if (UIApplication.sharedApplication.canOpenURL(reqUrl)) {
                            UIApplication.sharedApplication.openURL(reqUrl)
                        }
                        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                        return
                    }

                    decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                }
            }
            webView.navigationDelegate = navDelegate

            // Handle target=_blank by opening in the same webview
            val uiDelegate = object : NSObject(), WKUIDelegateProtocol {
                override fun webView(
                    webView: WKWebView,
                    createWebViewWithConfiguration: WKWebViewConfiguration,
                    forNavigationAction: WKNavigationAction,
                    windowFeatures: WKWindowFeatures
                ): WKWebView? {
                    val targetFrame = forNavigationAction.targetFrame
                    if (targetFrame == null || targetFrame.mainFrame == false) {
                        val request = forNavigationAction.request
                        webView.loadRequest(request)
                    }
                    return null
                }
            }
            webView.UIDelegate = uiDelegate
            webView.allowsBackForwardNavigationGestures = true
            NSURL.URLWithString(url)?.let { nsUrl ->
                webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
            }
            webView
        },
        update = { view ->
            val current = view.URL?.absoluteString
            if (current != url) {
                NSURL.URLWithString(url)?.let { nsUrl ->
                    view.loadRequest(NSURLRequest.requestWithURL(nsUrl))
                }
            }
        }
    )
}
