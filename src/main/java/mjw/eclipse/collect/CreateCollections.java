package mjw.eclipse.collect;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.Stacks;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.api.stack.MutableStack;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 02 Aug 2026, 9:23 AM
 */
public class CreateCollections {

    public void createList() {
        MutableList<Object> emptyList = Lists.mutable.empty();
        MutableList<String> list = Lists.mutable.with("1", "2", "3");

        ImmutableList<Object> emptyImmutableList = Lists.immutable.empty();
        ImmutableList<String> immutableList = Lists.immutable.with("1", "2", "3");
    }

    public void createSet() {
        MutableSet<Object> emptySet = Sets.mutable.empty();
        MutableSet<String> set = Sets.mutable.with("1", "2", "3");

        ImmutableSet<Object> emptyImmutableSet = Sets.immutable.empty();
        ImmutableSet<String> immutableSet = Sets.immutable.with("1", "2", "3");
    }

    public void createStack() {
        MutableStack<String> emptyStack = Stacks.mutable.empty();
        MutableStack<String> stack = Stacks.mutable.with("1", "2", "3");

        ImmutableStack<String> emptyImmutableStack = Stacks.immutable.empty();
        ImmutableStack<String> immutableStack = Stacks.immutable.with("1", "2", "3");
    }



}
