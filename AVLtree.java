public class AVLTree <T extends Comparable<T>> {

    public class Node {
        T key;
        Node left, right, parent;
        int height;

        Node(T key, Node parent) {
            this.key = key;
            this.parent = parent;
            this.height = 1;
        }
    }

    private Node root;


    public void insert(T key) {
        root = insert(root, key, null);
    }

    private Node insert(Node node, T key, Node parent) {
        if (node == null) return new Node(key, parent);

        if (key.compareTo(node.key) < 0) {
            node.left = insert(node.left, key, node);
        } else if (key.compareTo(node.key) > 0) {
            node.right = insert(node.right, key, node);
        }

        adjustHeight(node);
        rebalance(node);    // bug 1 fix: rebalanceo integrado en la recursión
        return node;
    }


    public void delete(T key) {
        Node toDelete = find(key);
        if (toDelete != null) {
            Node parent = toDelete.parent;
            root = delete(root, key);
            rebalance(parent);
        }
    }

    private Node delete(Node node, T key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
            if (node.left != null) node.left.parent = node;
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
            if (node.right != null) node.right.parent = node;
        } else {
            if (node.left == null && node.right == null) return null;

            if (node.left == null) {
                node.right.parent = node.parent;
                return node.right;
            } else if (node.right == null) {
                node.left.parent = node.parent;
                return node.left;
            }

            Node sig = findMin(node.right);
            node.key = sig.key;
            node.right = delete(node.right, sig.key);
            if (node.right != null) node.right.parent = node;
        }

        adjustHeight(node);
        return node;
    }


    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }


    public Node find(T key) {
        return find(root, key);
    }

    private Node find(Node node, T key) {
        if (node == null) return null;
        if (key.compareTo(node.key) == 0) return node;
        if (key.compareTo(node.key) < 0) return find(node.left, key);
        return find(node.right, key);
    }


    private void rebalance(Node node) {
        if (node == null) return;

        int leftHeight  = getHeight(node.left);
        int rightHeight = getHeight(node.right);

        if (leftHeight > rightHeight + 1) {
            rebalanceRight(node);
        } else if (rightHeight > leftHeight + 1) {
            rebalanceLeft(node);
        }

        adjustHeight(node);

        if (node.parent != null) {
            rebalance(node.parent);
        }
    }

    private void rebalanceRight(Node node) {
        Node left = node.left;
        if (getHeight(left.right) > getHeight(left.left)) {
            node.left = rotateLeft(left);
        }
        Node newRoot = rotateRight(node);            // bug 2 fix: guardar resultado
        if (newRoot.parent == null) root = newRoot;  // bug 2 fix: conectar si es raíz
    }

    private void rebalanceLeft(Node node) {
        Node right = node.right;
        if (getHeight(right.left) > getHeight(right.right)) {
            node.right = rotateRight(right);
        }
        Node newRoot = rotateLeft(node);             // bug 2 fix: guardar resultado
        if (newRoot.parent == null) root = newRoot;  // bug 2 fix: conectar si es raíz
    }


    private Node rotateLeft(Node node) {
        Node newNode = node.right;
        node.right = newNode.left;
        if (newNode.left != null) newNode.left.parent = node;
        newNode.left = node;

        newNode.parent = node.parent;
        node.parent = newNode;

        adjustHeight(node);
        adjustHeight(newNode);

        if (newNode.parent == null) root = newNode;
        return newNode;
    }

    private Node rotateRight(Node node) {
        Node newNode = node.left;
        node.left = newNode.right;
        if (newNode.right != null) newNode.right.parent = node;
        newNode.right = node;

        newNode.parent = node.parent;
        node.parent = newNode;

        adjustHeight(node);
        adjustHeight(newNode);

        if (newNode.parent == null) root = newNode;
        return newNode;
    }


    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    private void adjustHeight(Node node) {
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }


    public void printTree() {
        printTree(root, "", true);
    }

    private void printTree(Node node, String prefix, boolean isTail) {
        if (node == null) return;

        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.key);

        boolean hasLeft  = node.left  != null;
        boolean hasRight = node.right != null;

        if (hasLeft || hasRight) {
            if (hasRight)
                printTree(node.right, prefix + (isTail ? "   " : "│  "), false);
            if (hasLeft)
                printTree(node.left,  prefix + (isTail ? "   " : "│  "), true);
        }
    }


    public void printByResidency(boolean estado) {
        printByResidencyRec(root, estado);
    }

    private void printByResidencyRec(Node node, boolean estado) {  // bug 3 fix: Node en vez de Node<Estudiante>
        if (node == null) return;

        printByResidencyRec(node.left, estado);

        Estudiante e = (Estudiante) node.key;  // bug 3 fix: cast explícito, node.key en vez de node.data
        if (e.getHasResidency() == estado) {
            System.out.println(e);
        }

        printByResidencyRec(node.right, estado);
    }
}
