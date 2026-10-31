package org.example.generator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.example.classes.BinaryTreeNode;
import org.junit.jupiter.api.Test;

class RecursiveGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @Test
    void boundsRecursiveBinaryTreeGeneration() {
        BinaryTreeNode tree = generator.generateValueOfType(BinaryTreeNode.class);

        assertNotNull(tree.getData());
        assertNotNull(tree.getLeft());
        assertNotNull(tree.getRight());
        assertNotNull(tree.getLeft().getData());
        assertNotNull(tree.getRight().getData());
        assertNull(tree.getLeft().getLeft());
        assertNull(tree.getLeft().getRight());
        assertNull(tree.getRight().getLeft());
        assertNull(tree.getRight().getRight());
    }

    @Test
    void returnsNullWhenATypeExceedsItsRecursionLimit() {
        RecursiveNode root = generator.generateValueOfType(RecursiveNode.class);

        assertNotNull(root.child);
        assertNull(root.child.child);
    }

    static class RecursiveNode {
        private final RecursiveNode child;

        RecursiveNode(RecursiveNode child) {
            this.child = child;
        }
    }
}
