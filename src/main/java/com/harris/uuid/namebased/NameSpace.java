package com.harris.uuid.namebased;

/**
 * @description:
 * @author: KunQi Yu
 * @date: 2022-11-15 10:07
 **/
public interface NameSpace {

    /**
     * get name from NameSpace Randomly
     * @return
     */
    String randomName();

    /**
     * get name from Namespace with targeted name
     * @param name name with Namespace
     * @return true if name is allowed to distribute
     * false if name is not allowed
     */
    boolean getName(String name);
}
