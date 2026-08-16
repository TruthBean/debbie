/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.DataHelperFactory;
import com.truthbean.debbie.data.JsonHelper;
import com.truthbean.debbie.data.XmlHelper;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.BaseRouterRequest;

/**
 * Default implementation of {@link ErrorResponseCallback} that serializes
 * the response content to JSON or XML depending on the request's expected
 * response type.
 * <p>
 * Serialization helpers are loaded via SPI through {@link DataHelperFactory},
 * so this class has no compile-time dependency on any concrete serialization
 * library (e.g. Jackson). When no helper is available, the content is
 * serialized with {@link String#valueOf(Object)} as a fallback.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class DefaultErrorResponseCallback implements ErrorResponseCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorResponseCallback.class);

    @Override
    public void callback(BaseRouterRequest request, RouterResponse response) {
        Object data = response.getContent();
        response.setStatus(HttpStatus.OK);

        if (request.getResponseType().isSameMediaType(MediaType.APPLICATION_XML)) {
            response.setResponseType(request.getResponseType());
            response.setContent(serializeToXml(data));
        } else {
            response.setResponseType(MediaType.APPLICATION_JSON_UTF8);
            response.setContent(serializeToJson(data));
        }
    }

    private String serializeToJson(Object data) {
        JsonHelper jsonHelper = DataHelperFactory.getJsonHelper();
        if (jsonHelper != null) {
            return jsonHelper.toJson(data);
        }
        return String.valueOf(data);
    }

    private String serializeToXml(Object data) {
        XmlHelper xmlHelper = DataHelperFactory.getXmlHelper();
        if (xmlHelper != null) {
            return xmlHelper.toXml(data);
        }
        LOGGER.warn("no XmlHelper available, falling back to toString() for xml serialization");
        return String.valueOf(data);
    }
}
