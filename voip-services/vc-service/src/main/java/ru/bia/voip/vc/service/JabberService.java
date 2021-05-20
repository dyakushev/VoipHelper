package ru.bia.voip.vc.service;

import ru.bia.voip.vc.model.jabber.JabberType;
import ru.bia.voip.vc.model.jabber.rest.JabberAddResponse;
import ru.bia.voip.vc.model.jabber.rest.JabberResponse;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface JabberService {
    Optional<JabberAddResponse> add(String userName, Set<JabberType> jabberTypeList);

    Optional<Collection<JabberResponse>> getByUsername(String userName);

    Optional<Collection<JabberResponse>> getByType(JabberType jabberType);

    Optional<Collection<JabberResponse>> list();
}
