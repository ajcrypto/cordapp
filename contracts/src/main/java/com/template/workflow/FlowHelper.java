package com.template.workflow;

import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;

import java.util.ArrayList;
import java.util.List;

public class FlowHelper {

    public static List<Party> getAllCounterParties (List<AbstractParty> abstractPartyList, Party party, ServiceHub serviceHub){

    List<Party> parties = resolveIdentities(abstractPartyList,serviceHub);
    parties.remove(party);
    return parties;

    }

    private static List<Party> resolveIdentities(List<AbstractParty> abstractPartyList, ServiceHub serviceHub) {
        List<Party> allParties = new ArrayList<>();

        for (AbstractParty abstractParty: abstractPartyList){
            allParties.add(resolveIdentity(abstractParty,serviceHub));
        }

        return allParties;
    }

    private static Party resolveIdentity(AbstractParty abstractParty, ServiceHub serviceHub) {
        return serviceHub.getIdentityService().requireWellKnownPartyFromAnonymous(abstractParty);
    }

}
