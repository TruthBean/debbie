/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
plugins {
    id("de.jjohannes.extra-java-module-info") version "0.1"
}

extraJavaModuleInfo {
    module("metrics-healthchecks-4.1.11.jar", "io.dropwizard.metrics.healthchecks", "4.1.11")
    module("metrics-jvm-4.1.11.jar", "io.dropwizard.metrics.jvm", "4.1.11")
    module("metrics-core-4.1.11.jar", "io.dropwizard.metrics.core", "4.1.11")
    module("HdrHistogram-2.1.12.jar", "org.hdrhistogram.HdrHistogram", "2.1.12")
    module("LatencyUtils-2.0.3.jar", "org.latencyutils.LatencyUtils", "0.9.0")
    module("simpleclient-0.9.0.jar", "io.prometheus.simpleclient", "0.9.0")
}
dependencies {
    implementation("io.micrometer:micrometer-core:1.5.2")
    implementation("io.dropwizard.metrics:metrics-healthchecks:4.1.11")
    implementation("io.dropwizard.metrics:metrics-jvm:4.1.11")
    implementation("io.prometheus:simpleclient:0.9.0")

    testImplementation(project(":debbie-test"))
}