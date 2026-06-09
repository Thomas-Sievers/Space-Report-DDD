package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.SpaceEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree keyed on SpaceEvent intensity (used as riskScore proxy).
 * Provides O(log n) average-case lookup for events above a given threshold (RF-002).
 */
public class SpaceEventBST {

    private Node root;

    private static class Node {
        final SpaceEvent event;
        Node left;
        Node right;

        Node(SpaceEvent event) {
            this.event = event;
        }
    }

    public void insert(SpaceEvent event) {
        root = insertNode(root, event);
    }

    private Node insertNode(Node node, SpaceEvent event) {
        if (node == null) {
            return new Node(event);
        }
        if (event.getIntensity() < node.event.getIntensity()) {
            node.left = insertNode(node.left, event);
        } else {
            node.right = insertNode(node.right, event);
        }
        return node;
    }

    public List<SpaceEvent> findAboveThreshold(double threshold) {
        List<SpaceEvent> result = new ArrayList<>();
        collectAboveThreshold(root, threshold, result);
        return result;
    }

    private void collectAboveThreshold(Node node, double threshold, List<SpaceEvent> result) {
        if (node == null) return;
        if (node.event.getIntensity() > threshold) {
            result.add(node.event);
            collectAboveThreshold(node.left, threshold, result);
            collectAboveThreshold(node.right, threshold, result);
        } else {
            collectAboveThreshold(node.right, threshold, result);
        }
    }

    public List<SpaceEvent> inOrder() {
        List<SpaceEvent> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(Node node, List<SpaceEvent> result) {
        if (node == null) return;
        inOrderTraversal(node.left, result);
        result.add(node.event);
        inOrderTraversal(node.right, result);
    }
}
