package com.sky.slog;

import java.util.List;

/**
 * [一句话描述类的作用]
 * [详述类的功能。]
 * Created by sky on 2017/5/29.
 */

public interface TreeManager {
    /**
     * 返回其内部用于管理所有子树的根数
     */
    Tree asTree();

    /** Add a new logging tree. */
    void plantTree(Tree tree);

    /** Adds new logging trees. */
    void plantTrees(Tree... trees);

    /** Remove a planted tree. */
    void removeTree(Tree tree);

    /** Remove all planted trees. */
    void clearTrees();

    /** Return a copy of all planted {@linkplain Tree trees}. */
    List<Tree> forest();

    int treeCount();
}
