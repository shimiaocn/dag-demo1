package dag.worker;

import com.google.common.collect.Lists;
import dag.context.FlowContext;
import dag.worker.result.WorkResult;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IWorkExecutor<Param, Result> {

    /**
     * @param context 上下文对象，通过该对象可以获取所依赖节点的执行结果
     */
    Result run(Param param
            //, FlowContext context
    ) throws Exception;

    default List<WorkResult<?>> getDependsResult(Worker<?, ?> worker, FlowContext context) {
        Set<Worker<?, ?>> depends = worker.getDepends();
        if (CollectionUtils.isEmpty(depends)) {
            return Lists.newArrayList();
        }
        return depends.stream().map(Worker::getWorkResult).collect(Collectors.toList());
    }
}
