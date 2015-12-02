package com.xrosscode.tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Stack;

public class Tree {

    private final File root;

    public Tree(final String path) throws FileNotFoundException {
        this.root = new File(path);
        if (!this.root.exists()) {
            throw new FileNotFoundException(path);
        }
    }

    public void print() {
        final Stack<Node> stack = new Stack<Node>();
        stack.push(new Node(null, this.root, 0, 0, 0));

        while (!stack.isEmpty()) {
            final Node node = stack.pop();

            node.print();

            for (final Node child : node.getChildren()) {
                stack.push(child);
            }
        }
    }

    private static class Node {

        final Node parent;

        final File file;

        final int depth;

        final int nsibling;

        final int index;

        public Node(final Node parent, final File file, final int depth, final int nsibling, final int index) {
            this.parent = parent;
            this.file = file;
            this.depth = depth;
            this.nsibling = nsibling;
            this.index = index;
        }

        public Node[] getChildren() {
            if (!this.file.isDirectory())
                return new Node[0];

            final File[] files = this.file.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return !name.startsWith(".");
                }
            });

            if (null == files || files.length <= 0) {
                return new Node[0];
            }

            final Node[] children = new Node[files.length];

            for (int i = files.length - 1, j = 0; i >= 0; i--) {
                children[i] = new Node(this, files[i], this.depth + 1, files.length, j++);
            }

            return children;
        }

        public StringBuilder getIndent() {
            final StringBuilder builder;

            if (this.depth > 1) {
                builder = this.parent.getIndent();
                builder.append(this.parent.index + 1 >= this.parent.nsibling ? " " : "│");
                builder.append("   ");
            } else {
                builder = new StringBuilder();
            }

            return builder;
        }

        public void print() {
            if (this.depth <= 0) {
                System.out.println(this.file.getName());
            } else {
                final StringBuilder builder = this.depth > 1 ? this.getIndent() : new StringBuilder();
                builder.append(this.index + 1 >= this.nsibling ? "└" : "├");
                builder.append("── ").append(this.file.getName());
                System.out.println(builder);
            }
        }

    }

    public static void main(final String[] args) throws Exception {
        new Tree(null == args || args.length <= 0 ? System.getProperty("user.dir") : args[0]).print();
    }

}
