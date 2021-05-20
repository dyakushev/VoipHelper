package ru.bia.voip.phone.service;


import org.springframework.data.domain.Pageable;
import ru.bia.voip.phone.model.AbstractExtension;
import ru.bia.voip.phone.model.asterisk.AsteriskExtensionType;

import java.util.List;

/**
 * Represents operations on Asterisk
 *
 * @author Denis Yakushev
 * @version 1.0
 */

public interface AsteriskExtensionService<T extends AbstractExtension> extends ExtensionService {


    /**
     * Finds all the Extension objects
     *
     * @param pageable the object which controls size of output
     * @return List of Extension
     */
    List<T> findAll(Pageable pageable);

    /**
     * Finds all the Extension objects
     * uses findAll(Pageable pageable) with Pageable.unpaged;
     *
     * @return List of Extension
     */

    List<T> findAll();

    /**
     * Finds all the Extension objects with the given exten
     *
     * @param exten    the string represents extension
     * @param pageable the object which controls size of output
     * @return the list of Extension objects
     */
    List<T> findByExten(String exten, Pageable pageable);

    /**
     * The method finds all the Extension objects with the given exten
     *
     * @param exten the string represents extension
     * @return a list of Extension objects
     */
    List<T> findByExten(String exten);

    /**
     * Method finds all the Extension objects with the given id
     *
     * @param id the integer value which represents an extension
     * @param asteriskExtensionType enum, represents the type of extension
     * @return a list of Extension objects
     */
    List<T> getById(Integer id, AsteriskExtensionType asteriskExtensionType);


}
