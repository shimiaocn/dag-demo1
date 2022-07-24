package dag.flow;

import dag.worker.Worker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FlowChecker {

    private final Worker<?, ?> worker;
    @Setter
    @Getter
    private boolean beingVisited;
    @Setter
    @Getter
    private boolean visited;

    public static boolean hasCycle(Collection<Worker<?, ?>> nodes) {
        Map<Worker<?, ?>, FlowChecker> flowCheckerMap = nodes.stream().collect(Collectors.toMap(it -> it, FlowChecker::new));
        for (FlowChecker vertex : flowCheckerMap.values()) {
            List<String> way = new ArrayList<>();
            if (!vertex.isVisited() && hasCycle(vertex, flowCheckerMap, way)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasCycle(FlowChecker current, Map<Worker<?, ?>, FlowChecker> nodeCheckMap, List<String> way) {
        current.setBeingVisited(true);
        way.add(current.worker.getId());
        for (Worker<?, ?> node : current.worker.getDepends()) {
            FlowChecker neighbor = nodeCheckMap.get(node);
            if (neighbor.isBeingVisited()) {
                way.add(neighbor.worker.getId());
                log.warn("cycle: " + String.join("->", way));
                return true;
            }
            if (!neighbor.isVisited() && hasCycle(neighbor, nodeCheckMap, way)) {
                return true;
            }
        }
        current.setBeingVisited(false);
        current.setVisited(true);
        way.remove(way.size() - 1);
        return false;
    }
}
