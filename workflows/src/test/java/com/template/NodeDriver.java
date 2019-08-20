package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.node.User;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import static net.corda.testing.driver.Driver.driver;

/**
 * Allows you to run your nodes through an IDE (as opposed to using deployNodes). Do not use in a production
 * environment.
 */
public class NodeDriver {
    public static void main(String[] args) {
        final List<User> rpcUsers =
                ImmutableList.of(new User("user1", "test", ImmutableSet.of("ALL")));

        driver(new DriverParameters().withStartNodesInProcess(true).withWaitForAllNodesToFinish(true), dsl -> {
                    try {
                        NodeHandle nodeA = dsl.startNode(new NodeParameters()
                                .withProvidedName(new CordaX500Name("School1", "London", "GB"))
                                .withRpcUsers(rpcUsers)).get();
                        NodeHandle nodeB = dsl.startNode(new NodeParameters()
                                .withProvidedName(new CordaX500Name("School2", "New York", "US"))
                                .withRpcUsers(rpcUsers)).get();

                        dsl.startWebserver(nodeA);
                        dsl.startWebserver(nodeB);
                    } catch (Throwable e) {
                        System.err.println("Encountered exception in node startup: " + e.getMessage());
                        e.printStackTrace();
                    }

                    return null;
                }
        );
    }
}
