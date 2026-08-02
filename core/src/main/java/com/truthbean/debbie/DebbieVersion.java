/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie;

import com.truthbean.core.TruthBeanVersion;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-05-05 14:20
 */
public final class DebbieVersion extends TruthBeanVersion {
    private DebbieVersion() {
    }

    public static String getVersion() {
        return TruthBeanVersion.getVersion(DebbieVersion.class, "0.6.3-RELEASE");
    }
}
