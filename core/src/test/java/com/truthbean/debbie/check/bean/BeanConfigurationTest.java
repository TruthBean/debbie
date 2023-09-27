package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanConfiguration;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieBean;
import com.truthbean.transformer.DataTransformer;

@BeanConfiguration
public class BeanConfigurationTest {

    @DebbieBean(name = "dataTransformer")
    public DataTransformer<Integer, Character> integerCharacterDataTransformer() {
        return new DataTransformer<>() {
            @Override
            public Character transform(Integer integer) {
                return (char) integer.byteValue();
            }

            @Override
            public Integer reverse(Character character) {
                return (int) character;
            }
        };
    }

    @DebbieBean(name = "hehe")
    public Object hahahaha() {
        class Inner {
            private String aaa = "少年包青天";
        }
        return new Inner() {
            public void hello() {
                System.out.println("匿名内部类...");
            }
        };
    }

    @DebbieBean(name = "大帅比")
    public Object handsomeGuy() {
        return "大帅比";
    }

    @DebbieBean(name = "我是谁")
    public Object who(@BeanInject("大帅比") Object handsomeGuy) {
        return "我是" + handsomeGuy;
    }

    @DebbieBean(name = "狄青")
    public Inner 狄青() {
        return new Inner() {
            public void haha() {
                System.out.println("匿名内部类...");
            }
        };
    }

    @DebbieBean
    public 幽灵 幽灵() {
        return () -> System.out.println("匿名内部类...");
    }

    private class Inner {
        public void hello() {
            System.out.println("匿名内部类...");
        }
    }

    private interface 幽灵 {
        void v();
    }

}
