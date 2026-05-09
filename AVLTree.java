public class AVLTree <T extends Comparable<T>> {

    private Node root;

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

    ////////////////////////////////////////// ----> METODOS EXTRA PROYECTO
    
    public T searchById(long id) {
        return searchByIdRec(root, id);
    }

    private T searchByIdRec(Node node, long id) {//(version recursiva)
        if (node == null) {
            return null;
        }

        Estudiante estudianteActual = (Estudiante) node.key;

        if (id == estudianteActual.getId()) {
            return node.key;
        }

        if (id < estudianteActual.getId()) {
            return searchByIdRec(node.left, id);
        } else {
            return searchByIdRec(node.right, id);
        }
    }

    public void printByResidency(boolean estado) {
        printByResidencyRec(root, estado);
    }

    private void printByResidencyRec(Node node, boolean estado) {//(version recursiva)
        if (node == null) return;

        printByResidencyRec(node.left, estado);

        Estudiante e = (Estudiante) node.key;  
        if (e.getHasResidency() == estado) {
            System.out.println(e);
        }

        printByResidencyRec(node.right, estado);
    }

    ////////////////////////////////////////// ----> METODOS PROPIOS DE AVLTREE
    
    public void insert(T key) {
        root = insert(root, key, null);
    }

    public Node find(T key) {
        return find(root, key);
    }

    public void delete(T key) {
        Node toDelete = find(key);
        if (toDelete != null) {
            Node parent = toDelete.parent;
            root = delete(root, key);
            rebalance(parent);
        }
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    private void adjustHeight(Node node) {
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private Node find(Node node, T key) {//(version recursiva)
        if (node == null) return null;
        if (key.compareTo(node.key) == 0) return node;
        if (key.compareTo(node.key) < 0) return find(node.left, key);
        return find(node.right, key);
    }

    private Node insert(Node node, T key, Node parent) {
    if (node == null) return new Node(key, parent);

    if (key.compareTo(node.key) < 0) {
        node.left = insert(node.left, key, node);
    } else if (key.compareTo(node.key) > 0) {
        node.right = insert(node.right, key, node);
    }

    adjustHeight(node);
    return rebalance(node);  // ← usar el resultado directamente
    }
    
    private Node delete(Node node, T key) {//(version recursiva)
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
        return rebalance(node);
    }

    private Node rebalance(Node node) {
    if (node == null) return null;

    int leftHeight  = getHeight(node.left);
    int rightHeight = getHeight(node.right);

    if (leftHeight > rightHeight + 1) {
        return rebalanceRight(node);  // ← retornar
    } else if (rightHeight > leftHeight + 1) {
        return rebalanceLeft(node);   // ← retornar
    }

    adjustHeight(node);
    return node;  // ← retornar
}

private Node rebalanceRight(Node node) {
    Node left = node.left;
    if (getHeight(left.right) > getHeight(left.left)) {
        node.left = rotateLeft(left);
        node.left.parent = node;
    }
    Node newRoot = rotateRight(node);
    if (newRoot.parent == null) root = newRoot;
    return newRoot;  // ← retornar
}

private Node rebalanceLeft(Node node) {
    Node right = node.right;
    if (getHeight(right.left) > getHeight(right.right)) {
        node.right = rotateRight(right);
        node.right.parent = node;
    }
    Node newRoot = rotateLeft(node);
    if (newRoot.parent == null) root = newRoot;
    return newRoot;  // ← retornar
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

}
