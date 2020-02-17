package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean"))
@ExtendWith({DebbieApplicationExtension.class})
public class UndertowApplicationTest {

    @Test
    public void content() {
        System.out.println("nothing");
    }
}
