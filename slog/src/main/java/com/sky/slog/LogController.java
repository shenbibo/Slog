package com.sky.slog;

import android.support.annotation.Nullable;

import java.util.*;

import static com.sky.slog.Slog.*;
import static java.util.Collections.unmodifiableList;

/**
 * 日志控制器，包括日志的分发，日志树的管理
 * <p>
 * Created by sky on 2017/5/29.
 */
interface LogController extends LogDispatcher, TreeManager {

    class LogManagerImpl implements LogController {
        private final Tree[] TREE_ARRAY_EMPTY = new Tree[0];
        private volatile Tree[] forestAsArray = TREE_ARRAY_EMPTY;
        // Both fields guarded by 'FOREST'.
        private final List<Tree> FOREST = new ArrayList<>();

        @Override
        public void log(int priority, String tag, Throwable t, String[] compoundMessages, @Nullable String originalMsg,
                @Nullable Object... args) {
            switch (priority) {
                case DEBUG:
                    SOULS_TREE.d(tag, t, compoundMessages, originalMsg, args);
                    break;

                case INFO:
                    SOULS_TREE.i(tag, t, compoundMessages, originalMsg, args);
                    break;

                case WARN:
                    SOULS_TREE.w(tag, t, compoundMessages, originalMsg, args);
                    break;

                case ERROR:
                    SOULS_TREE.e(tag, t, compoundMessages, originalMsg, args);
                    break;

                case VERBOSE:
                    SOULS_TREE.v(tag, t, compoundMessages, originalMsg, args);
                    break;

                case ASSERT:
                    SOULS_TREE.wtf(tag, t, compoundMessages, originalMsg, args);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void log(int priority, String tag, String[] compoundMessages, @Nullable Object originalObject) {
            switch (priority) {
                case DEBUG:
                    SOULS_TREE.d(tag, compoundMessages, originalObject);
                    break;

                case INFO:
                    SOULS_TREE.i(tag, compoundMessages, originalObject);
                    break;

                case WARN:
                    SOULS_TREE.w(tag, compoundMessages, originalObject);
                    break;

                case ERROR:
                    SOULS_TREE.e(tag, compoundMessages, originalObject);
                    break;

                case VERBOSE:
                    SOULS_TREE.v(tag, compoundMessages, originalObject);
                    break;

                case ASSERT:
                    SOULS_TREE.wtf(tag, compoundMessages, originalObject);
                    break;

                default:
                    break;
            }
        }

        @Override
        public Tree asTree() {
            return SOULS_TREE;
        }

        @Override
        public void plantTree(Tree tree) {
            if (tree == null) {
                throw new NullPointerException("tree == null");
            }
            if (tree == SOULS_TREE) {
                throw new IllegalArgumentException("Cannot plant Tree into itself.");
            }
            synchronized (FOREST) {
                FOREST.add(tree);
                forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
            }
        }

        @Override
        public void plantTrees(Tree... trees) {
            if (trees == null) {
                throw new NullPointerException("trees == null");
            }
            for (Tree tree : trees) {
                if (tree == null) {
                    throw new NullPointerException("trees contains null");
                }
                if (tree == SOULS_TREE) {
                    throw new IllegalArgumentException("Cannot plant Tree into itself.");
                }
            }
            synchronized (FOREST) {
                Collections.addAll(FOREST, trees);
                forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
            }
        }

        @Override
        public void removeTree(Tree tree) {
            synchronized (FOREST) {
                if (!FOREST.remove(tree)) {
                    return;
                }
                forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
            }
        }

        @Override
        public void clearTrees() {
            synchronized (FOREST) {
                FOREST.clear();
                forestAsArray = TREE_ARRAY_EMPTY;
            }
        }

        @Override
        public List<Tree> forest() {
            synchronized (FOREST) {
                return unmodifiableList(new ArrayList<>(FOREST));
            }
        }

        @Override
        public int treeCount() {
            synchronized (FOREST) {
                return FOREST.size();
            }
        }

        private final Tree SOULS_TREE = new Tree() {
            @Override
            public void v(String tag, Throwable t, String[] compoundMessages,
                    @Nullable String originalMessage, @Nullable Object... args) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.v(tag, t, compoundMessages, originalMessage, args);
                }
            }

            @Override
            public void v(String tag, String[] compoundMessages, @Nullable Object object) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.v(tag, compoundMessages, object);
                }
            }

            @Override
            public void d(String tag, Throwable t, String[] compoundMessages,
                    @Nullable String originalMessage, @Nullable Object... args) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.d(tag, t, compoundMessages, originalMessage, args);
                }
            }

            @Override
            public void d(String tag, String[] compoundMessages, @Nullable Object object) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.d(tag, compoundMessages, object);
                }
            }

            @Override
            public void i(String tag, Throwable t, String[] compoundMessages,
                    @Nullable String originalMessage, @Nullable Object... args) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.i(tag, t, compoundMessages, originalMessage, args);
                }
            }

            @Override
            public void i(String tag, String[] compoundMessages, @Nullable Object object) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.i(tag, compoundMessages, object);
                }
            }

            @Override
            public void w(String tag, Throwable t, String[] compoundMessages,
                    @Nullable String originalMessage, @Nullable Object... args) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.w(tag, t, compoundMessages, originalMessage, args);
                }
            }

            @Override
            public void w(String tag, String[] compoundMessages, @Nullable Object object) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.w(tag, compoundMessages, object);
                }
            }

            @Override
            public void e(String tag, Throwable t, String[] compoundMessages,
                    @Nullable String originalMessages, @Nullable Object... args) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.e(tag, t, compoundMessages, originalMessages, args);
                }
            }

            @Override
            public void e(String tag, String[] compoundMessages, @Nullable Object object) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.e(tag, compoundMessages, object);
                }
            }

            @Override
            public void wtf(String tag, Throwable t, String[] compoundMessages,
                    @Nullable String originalMessages, @Nullable Object... args) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.wtf(tag, t, compoundMessages, originalMessages, args);
                }
            }

            @Override
            public void wtf(String tag, String[] compoundMessages, @Nullable Object object) {
                Tree[] forest = forestAsArray;
                for (Tree tree : forest) {
                    tree.wtf(tag, compoundMessages, object);
                }
            }

            @Override
            protected void log(int priority, String tag, String message) {
                throw new UnsupportedOperationException("not support log(int priority, String tag, String message) method");
            }
        };
    }
}
