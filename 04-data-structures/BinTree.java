class BinTree {
    public static BinNode buildTestTree() {
        return new BinNode(2,
            new BinNode(1,
                new BinNode(4,
                    new BinNode(7),
                    null
                ),
                new BinNode(5,
                    null,
                    new BinNode(8)
                )
            ),
            new BinNode(3,
                null,
                new BinNode(6,
                    new BinNode(9),
                    new BinNode(10)
                )
            )
        );
    }

    public static BinNode buildFromMermaid(String file, String name) {
        return null;
    }

    public static boolean hasValue2(BinNode node, int value) {
        if (node == null) {
            return false;
        }

        var containsLeft = hasValue(node.getLeft(), value);

        if (containsLeft) {
            return true;
        }

        var containsRight = hasValue(node.getRight(), value);

        return containsRight;
    }

    public static boolean hasValue(BinNode node, int value) {
        if (node == null) {
            return false;
        }

        if (node.getValue() == value) {
            return true;
        }

        return 
            hasValue(node.getLeft(), value) ||
            hasValue(node.getRight(), value);
    }


    public static void print(BinNode node) {
        if (node == null) {
            return;
        }

        System.out.println(node);

        print(node.getLeft());
        print(node.getRight());
    }

    public static int sumPreOrder(BinNode node) {
        if (node == null) {
            return 0;
        }

        return 
            node.getValue() +
            sumPreOrder(node.getLeft()) +
            sumPreOrder(node.getRight());
    }

    public static int sumInOrder(BinNode node) {
        if (node == null) {
            return 0;
        }

        return
            sumInOrder(node.getLeft()) +
            node.getValue() +
            sumInOrder(node.getRight());
    }


    public static int sumPostOrder(BinNode node) {
        if (node == null) {
            return 0;
        }

        return
            sumPostOrder(node.getLeft()) +
            sumPostOrder(node.getRight()) +
            node.getValue();
    }
}
