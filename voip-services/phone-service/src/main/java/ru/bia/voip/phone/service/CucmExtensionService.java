package ru.bia.voip.phone.service;

import ru.bia.voip.phone.model.AbstractExtension;

import java.util.List;

public interface CucmExtensionService<T extends AbstractExtension> extends ExtensionService {
    List<T> findByExten(String exten);
}
