package com.truthbean.debbie.graalvm;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/23 11:47.
 */
public class ReflectConfigJsonCreater {

    public static void main(String[] args) {
        String all = """
                io.undertow.client.http.HttpClientProvider
                io.undertow.client.ajp.AjpClientProvider
                io.undertow.client.http2.Http2ClientProvider
                io.undertow.client.http2.Http2ClearClientProvider
                io.undertow.client.http2.Http2PriorKnowledgeClientProvider
                io.undertow.predicate.PathMatchPredicate$Builder
                io.undertow.predicate.PathPrefixPredicate$Builder
                io.undertow.predicate.ContainsPredicate$Builder
                io.undertow.predicate.ExistsPredicate$Builder
                io.undertow.predicate.RegularExpressionPredicate$Builder
                io.undertow.predicate.PathSuffixPredicate$Builder
                io.undertow.predicate.EqualsPredicate$Builder
                io.undertow.predicate.PathTemplatePredicate$Builder
                io.undertow.predicate.MethodPredicate$Builder
                io.undertow.predicate.AuthenticationRequiredPredicate$Builder
                io.undertow.predicate.MaxContentSizePredicate$Builder
                io.undertow.predicate.MinContentSizePredicate$Builder
                io.undertow.predicate.SecurePredicate$Builder
                io.undertow.predicate.IdempotentPredicate$Builder
                io.undertow.predicate.RequestLargerThanPredicate$Builder
                io.undertow.predicate.RequestSmallerThanPredicate$Builder
                io.undertow.protocols.ssl.SNIAlpnEngineManager
                io.undertow.protocols.alpn.DefaultAlpnEngineManager
                io.undertow.protocols.alpn.JDK8HackAlpnProvider
                io.undertow.protocols.alpn.JettyAlpnProvider
                io.undertow.protocols.alpn.JDK9AlpnProvider
                io.undertow.protocols.alpn.OpenSSLAlpnProvider
                io.undertow.server.handlers.builder.RewriteHandlerBuilder
                io.undertow.server.handlers.SetAttributeHandler$Builder
                io.undertow.server.handlers.SetAttributeHandler$ClearBuilder
                io.undertow.server.handlers.builder.ResponseCodeHandlerBuilder
                io.undertow.server.handlers.DisableCacheHandler$Builder
                io.undertow.server.handlers.ProxyPeerAddressHandler$Builder
                io.undertow.server.handlers.proxy.ProxyHandlerBuilder
                io.undertow.server.handlers.RedirectHandler$Builder
                io.undertow.server.handlers.accesslog.AccessLogHandler$Builder
                io.undertow.server.handlers.AllowedMethodsHandler$Builder
                io.undertow.server.handlers.BlockingHandler$Builder
                io.undertow.server.handlers.CanonicalPathHandler$Builder
                io.undertow.server.handlers.DisallowedMethodsHandler$Builder
                io.undertow.server.handlers.error.FileErrorPageHandler$Builder
                io.undertow.server.handlers.HttpTraceHandler$Builder
                io.undertow.server.JvmRouteHandler$Builder
                io.undertow.server.handlers.PeerNameResolvingHandler$Builder
                io.undertow.server.handlers.RequestDumpingHandler$Builder
                io.undertow.server.handlers.RequestLimitingHandler$Builder
                io.undertow.server.handlers.resource.ResourceHandler$Builder
                io.undertow.server.handlers.SSLHeaderHandler$Builder
                io.undertow.server.handlers.ResponseRateLimitingHandler$Builder
                io.undertow.server.handlers.URLDecodingHandler$Builder
                io.undertow.server.handlers.PathSeparatorHandler$Builder
                io.undertow.server.handlers.IPAddressAccessControlHandler$Builder
                io.undertow.server.handlers.ByteRangeHandler$Builder
                io.undertow.server.handlers.encoding.EncodingHandler$Builder
                io.undertow.server.handlers.encoding.RequestEncodingHandler$Builder
                io.undertow.server.handlers.LearningPushHandler$Builder
                io.undertow.server.handlers.SetHeaderHandler$Builder
                io.undertow.predicate.PredicatesHandler$DoneHandlerBuilder
                io.undertow.predicate.PredicatesHandler$RestartHandlerBuilder
                io.undertow.server.handlers.RequestBufferingHandler$Builder
                io.undertow.server.handlers.StuckThreadDetectionHandler$Builder
                io.undertow.server.handlers.AccessControlListHandler$Builder
                io.undertow.server.handlers.JDBCLogHandler$Builder
                io.undertow.server.handlers.LocalNameResolvingHandler$Builder
                io.undertow.server.handlers.StoredResponseHandler$Builder
                io.undertow.server.handlers.SecureCookieHandler$Builder
                io.undertow.server.handlers.ForwardedHandler$Builder
                io.undertow.server.handlers.HttpContinueAcceptingHandler$Builder
                io.undertow.server.handlers.form.EagerFormParsingHandler$Builder
                io.undertow.server.handlers.SameSiteCookieHandler$Builder
                io.undertow.server.handlers.SetErrorHandler$Builder""";
        String[] classNames = all.split("\n");

        for (String className : classNames) {
            System.out.println("{\n" +
                    "    \"name\": \"" + className + "\",\n" +
                    "    \"methods\": [\n" +
                    "      { \"name\": \"<init>\", \"parameterTypes\": [] }\n" +
                    "    ],\n" +
                    "    \"allDeclaredConstructors\" : true,\n" +
                    "    \"allPublicConstructors\" : true,\n" +
                    "    \"allDeclaredMethods\" : true,\n" +
                    "    \"allPublicMethods\" : true,\n" +
                    "    \"allDeclaredFields\" : true,\n" +
                    "    \"allPublicFields\" : true\n" +
                    "  },");
        }
    }
}
