package ru.itlab.menuservice.storage.util;

import org.hibernate.proxy.HibernateProxy;

public class HibernateUtil {
    public static Class<?> getEffectiveClass(Object o) {
        return o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    }
}
