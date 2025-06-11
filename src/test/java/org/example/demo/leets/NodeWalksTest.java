package org.example.demo.leets;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeWalksTest {

    @Test
    void walks_abc() {
        List<String> result = new NodeWalks().walks("abc");
        System.out.println(result);
    }

    @Test
    void walks_ab() {
        List<String> result = new NodeWalks().walks("ab");
        System.out.println(result);
    }

    @Test
    void walks_abcde() {
        List<String> result = new NodeWalks().walks("abcde");
        System.out.println(result);
    }
}