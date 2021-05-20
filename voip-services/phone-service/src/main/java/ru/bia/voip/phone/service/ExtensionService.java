package ru.bia.voip.phone.service;

import ru.bia.voip.phone.model.AbstractExtension;

public interface ExtensionService {

    /**
     * Method changes an  {@link ru.bia.voip.phone.model.AbstractExtension AbstractExtension.class}  based on Strings
     *
     * @param fromExtensionString the string which represents from extension
     * @param toExtensionString   the string represents to extension
     * @return Changed extension object
     */

    AbstractExtension changeExtension(String fromExtensionString, String toExtensionString);

    /**
     * Method changes {@link ru.bia.voip.phone.model.AbstractExtension AbstractExtension.class} based on Object and string
     *
     * @param fromExtension     the object which represents from extension
     * @param toExtensionString the string represents to extension
     * @return Changed extension object
     */

    AbstractExtension changeExtension(AbstractExtension fromExtension, String toExtensionString);


}
