package com.lswmobile.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.CoreGraphics.CGRectZero
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
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
            val webView = WKWebView(frame = CGRectZero.readValue(), configuration = config)
            val delegate = object : NSObject(), WKNavigationDelegateProtocol {
                override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                    onUpdateTitle?.invoke(webView.title)
                    onProgress?.invoke(1f)
                }
            }
            webView.navigationDelegate = delegate
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
