package org.drools.spring.metadata;

import java.util.HashMap;
import java.util.Map;

import org.drools.spring.metadata.MethodMetadataSource;
import org.drools.spring.metadata.NameMatchMethodMetadataSource;

public class NameMatchMethodMetadataSourceTest extends MethodMetadataSourceTestCase {

    protected MethodMetadataSource getSourceUnderTest() {
        Map nameMap = new HashMap();
        nameMap.put("publicBooleanMethodWithParameters", NameMatchMethodMetadataSource.CONDITION_METADATA);
        nameMap.put("publicVoidMethodWithNoParameters", NameMatchMethodMetadataSource.CONSEQUENCE_METADATA);
        nameMap.put("publicVoidMethodWithParameters", NameMatchMethodMetadataSource.CONSEQUENCE_METADATA);
        NameMatchMethodMetadataSource source = new NameMatchMethodMetadataSource();
        source.setNameMap(nameMap);
        return source;
    }
}