package net.sourceforge.fenixedu.domain.util.email;

import net.sourceforge.fenixedu.domain.util.Email;

import org.fenixedu.bennu.core.domain.Bennu;

public class MessageTransportResult extends MessageTransportResult_Base {

    public MessageTransportResult(final Email email, final Integer code, final String description) {
        super();
        setRootDomainObject(Bennu.getInstance());
        setEmail(email);
        setCode(code);
        setDescription(description);
    }

    public void delete() {
        setEmail(null);
        setRootDomainObject(null);
        deleteDomainObject();
    }

    @Deprecated
    public boolean hasDescription() {
        return getDescription() != null;
    }

    @Deprecated
    public boolean hasBennu() {
        return getRootDomainObject() != null;
    }

    @Deprecated
    public boolean hasEmail() {
        return getEmail() != null;
    }

    @Deprecated
    public boolean hasCode() {
        return getCode() != null;
    }

}
