/**
 * Created by jmcghee on 6/10/16.
 */
public class BinaryTreeNode {

    private int data;
    private BinaryTreeNode left;
    private BinaryTreeNode right;

    public BinaryTreeNode (int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public BinaryTreeNode (int[] data) {
        if (data.length > 0) {
            this.data = data[0];
            for (int i = 1; i < data.length; i++) {
                try {
                    this.insert(data[i]);
                } catch (Exception e) {
                    System.out.println("Item: " + data[i] + " already exists in tree.");
                }
            }
        }
    }

    public BinaryTreeNode find (int x) {
        if (this.data == x) return this;
        if (x < this.data) {
            if (this.left == null) return null;
            return this.left.find(x);
        }
        if (this.right == null) return null;
        return this.right.find(x);
    }

    public BinaryTreeNode findParent (int x) {
        if (this.data == x) return null;
        if ((this.left != null && this.left.data == x) || (this.right != null && this.right.data == x)) {
            return this;
        }
        if (x < this.data) {
            if (this.left == null) return null;
            return this.left.findParent(x);
        }
        if (this.right == null) return null;
        return this.right.findParent(x);
    }

    public void insert (int x) throws Exception {
        if (x < this.data) {
            if (this.left == null) {
                this.left = new BinaryTreeNode(x);
                return;
            }
            this.left.insert(x);
            return;
        } else if (x > this.data) {
            if (this.right == null) {
                this.right = new BinaryTreeNode(x);
                return;
            }
            this.right.insert(x);
            return;
        }
        throw new Exception("Value already exists in this tree.");
    }

    public BinaryTreeNode minimum () {
        if (this.left == null) return this;
        return this.left.minimum();
    }

    public BinaryTreeNode maximum () {
        if (this.right == null) return this;
        return this.right.maximum();
    }

    public BinaryTreeNode ceil (int x) {
        if (this.data == x) return this;
        if (x < this.data) {
            if (this.left == null || this.left.ceil(x) == null) return this;
            return this.left.ceil(x);
        }
        if (this.right == null) return null;
        return this.right.ceil(x);
    }

    public BinaryTreeNode floor (int x) {
        if (this.data == x) return this;
        if (x > this.data) {
            if (this.right == null || this.right.floor(x) == null) return this;
            return this.right.floor(x);
        }
        if (this.left == null) return null;
        return this.left.floor(x);
    }

    public void deleteMin () throws Exception {
        if (this.left == null) {
            if (this.right == null) {
                throw new Exception("Cannot delete last node.");
            }
            this.data = this.right.data;
            this.left = this.right.left;
            this.right = this.right.right;
            return;
        } else if (this.left.isLeaf()) {
            this.left = null;
        } else {
            this.left.deleteMin();
        }
    }

    public void deleteMax () throws Exception {
        if (this.right == null) {
            if (this.left == null) {
                throw new Exception("Cannot delete last node.");
            }
            this.data = this.left.data;
            this.left = this.left.left;
            this.right = this.left.right;
        } else if (this.right.isLeaf()) {
            this.right = null;
        } else {
            this.right.deleteMax();
        }
    }

    public void delete (int x) throws Exception {
        if (this.isLeaf()) {
            throw new Exception("Cannot delete last node.");
        }

        if (this.data == x) {
            if (this.left == null || this.right == null) {
                BinaryTreeNode btn = this.left == null ? this.right : this.left;
                this.data = btn.data;
                this.left = btn.left;
                this.right = btn.right;
            } else {
                BinaryTreeNode rightMin = this.right.minimum();
                this.right.deleteMin();
                this.data = rightMin.getData();
            }
        } else {
            BinaryTreeNode t = this.findParent(x);
            if (t != null) {
                if (t.left != null && t.left.data == x) {
                    if (t.left.isLeaf()) t.left = null;
                    else t.left.delete(x);
                } else if (t.right != null && t.right.data == x) {
                    if (t.right.isLeaf()) t.right = null;
                    else t.right.delete(x);
                }
            }
        }

    }

    public boolean isLeaf() {
        return this.right == null && this.left == null;
    }

    public int getData() {
        return this.data;
    }
}
