package com.example.crawler.dns;

import org.apache.commons.validator.routines.DomainValidator;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;
import org.xbill.DNS.ARecord;

public class DnsResolver {
    private static final String DEFAULT_DNS = "8.8.8.8";

    public static String resolve(String domain) throws Exception {
        if (!DomainValidator.getInstance().isValid(domain)) {
            throw new IllegalArgumentException("Invalid domain format");
        }

        SimpleResolver resolver = new SimpleResolver(DEFAULT_DNS);
        Lookup lookup = new Lookup(domain, Type.A);
        lookup.setResolver(resolver);
        org.xbill.DNS.Record[] records = lookup.run(); // 明确指定类路径

        return records.length > 0 ?
                ((ARecord)records[0]).getAddress().getHostAddress() :
                null;
    }
}