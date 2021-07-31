import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Graph {

    private HashMap<Repository, List<Committer>> m_adjListCommitters;
    private HashMap<Repository, Integer> m_adjListForks;

    Graph(List<Edge> edges)
    {
        m_adjListCommitters = new HashMap<>();
        m_adjListForks = new HashMap<>();
        InitCommitterGraph(edges);
        InitForksGraph(edges);
        printTopTenCommitters(sortByCommitter(), "committer count");
        printTopTenForks(sortByForks(), "fork count");
        printTopTenCommittersForkWeighted("fork-weighted");
    }

    private void InitForksGraph(List<Edge> edges) {
        for(Edge edge : edges)
        {
            m_adjListForks.put(edge.getRepo(), edge.getRepo().getNumOfForks());
        }
    }

    private void InitCommitterGraph(List<Edge> edges) {
        for(Edge edge : edges)
        {
            if(!m_adjListCommitters.containsKey(edge.getRepo()))
            {
                m_adjListCommitters.put(edge.getRepo(), new ArrayList<>());
            }
            m_adjListCommitters.get(edge.getRepo()).add(edge.getCommitter());
        }
    }

    public LinkedHashMap<Repository, List<Committer>> sortByCommitter() {
        Map<Repository, List<Committer>> sorted = m_adjListCommitters.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().size()))
                .collect(toMap(
                   Map.Entry::getKey,
                   Map.Entry::getValue,
                        (a, b) -> {throw new AssertionError();},
                        LinkedHashMap::new
                ));
        return (LinkedHashMap<Repository, List<Committer>>) sorted;
    }

    public LinkedHashMap<Repository, Integer> sortByForks() {
        Map<Repository, Integer> sorted = m_adjListForks.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {throw new AssertionError();},
                        LinkedHashMap::new
                ));
        return (LinkedHashMap<Repository, Integer>) sorted;
    }

    public LinkedHashMap<Repository, List<Committer>> sortByNorm() {
        Map<Repository, List<Committer>> sorted = m_adjListCommitters.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getKey().getNorm()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {throw new AssertionError();},
                        LinkedHashMap::new
                ));
        return (LinkedHashMap<Repository, List<Committer>>) sorted;
    }

    private void printTopTenCommitters(LinkedHashMap<Repository, List<Committer>> toPrint, String msg)
    {
        ArrayList<String> repos = new ArrayList<>();

        List<Map.Entry<Repository, List<Committer>>> list = new ArrayList<>(toPrint.entrySet());
        System.out.println("############# Top 10 By " + msg + " #############");
        for(int i = list.size() - 1, j = 10 ; j > 0 ; i--)
        {
            Map.Entry<Repository, List<Committer>> entry = list.get(i);
            if(!repos.contains(entry.getKey().getRepoProject()))
            {
                entry.getKey().setNorm(normalizeByCommitter(entry.getValue().size()));
                System.out.println(entry.getKey());
                repos.add(entry.getKey().getRepoProject());
                j--;
            }
        }
    }

    private void printTopTenCommittersForkWeighted(String msg)
    {
        Map.Entry<Repository, List<Committer>> e;
        float totalForks = getTotalForks();
        float totalCommitters = getTotalCommitters();

        for(Map.Entry<Repository, List<Committer>> entry : m_adjListCommitters.entrySet())
        {
            int xi = getTopCommittersForRepo(entry.getKey());
            int xy = getTopForksForRepo(entry.getKey());
            entry.getKey().setNorm(normalizeByForksWeight(xi, xy));
        }
        ArrayList<String> repos = new ArrayList<>();

        List<Map.Entry<Repository, List<Committer>>> list = new ArrayList<>(sortByNorm().entrySet());
        System.out.println("############# Top 10 By " + msg + " #############");
        for(int i = list.size() - 1, j = 10 ; j > 0 ; i--)
        {
            Map.Entry<Repository, List<Committer>> entry = list.get(i);
            if(!repos.contains(entry.getKey().getRepoProject()))
            {
                entry.getKey().setNumCommitters(getTopCommittersForRepo(entry.getKey()));
                entry.getKey().setNumOfForks(getTopForksForRepo(entry.getKey()));
                System.out.println(entry.getKey());
                repos.add(entry.getKey().getRepoProject());
                j--;
            }
        }
    }

    private void printTopTenForks(LinkedHashMap<Repository, Integer> toPrint, String msg)
    {
        ArrayList<String> repos = new ArrayList<>();

        List<Map.Entry<Repository, Integer>> list = new ArrayList<>(toPrint.entrySet());
        System.out.println("############# Top 10 By " + msg + " #############");
        for(int i = list.size() - 1, j = 10 ; j > 0 ; i--)
        {
            Map.Entry<Repository, Integer> entry = list.get(i);
            if(!repos.contains(entry.getKey().getRepoProject()))
            {
                entry.getKey().setNumCommitters(getTopCommittersForRepo(entry.getKey()));
                entry.getKey().setNorm(normalizeByForks(entry.getValue()));
                System.out.println(entry.getKey());
                repos.add(entry.getKey().getRepoProject());
                j--;
            }
        }
    }

    private float normalizeByCommitter(float xi)
    {
        float max = getMaxCommittersForRepo(m_adjListCommitters);
        float min = getMinCommittersForRepo(m_adjListCommitters);
        return (xi - min)/(max - min); //zi
    }

    private float normalizeByForks(float xi)
    {
        float max = getMaxForksForRepo(m_adjListForks);
        float min = getMinMinForRepo(m_adjListForks);
        return (xi - min)/(max - min); //zi
    }

    private float normalizeByForksWeight(float xi, float xj)
    {
        float max = getMaxForksForRepo(m_adjListForks);
        float min = getMinMinForRepo(m_adjListForks);
        float maxC = getMaxCommittersForRepo(m_adjListCommitters);
        float minC = getMinCommittersForRepo(m_adjListCommitters);
        return ((xi + xj) - (min + minC))/((max + maxC) - (min + minC)); //zi
    }

    private float getTotalForks()
    {
        float toReturn = 0;
        ArrayList<String> repositories = new ArrayList<>();

        for(Repository repository : m_adjListForks.keySet())
        {
            if(!repositories.contains(repository.getRepoProject()))
            {
                toReturn += getTopForksForRepo(repository);
                repositories.add(repository.getRepoProject());
            }
        }

        return toReturn;
    }

    private float getTotalCommitters()
    {
        float toReturn = 0;
        ArrayList<String> repositories = new ArrayList<>();

        for(Repository repository : m_adjListCommitters.keySet())
        {
            if(!repositories.contains(repository.getRepoProject()))
            {
                toReturn += getTopCommittersForRepo(repository);
                repositories.add(repository.getRepoProject());
            }
        }

        return toReturn;
    }

    private float getMaxCommittersForRepo(HashMap<Repository, List<Committer>> getMax) {

        float max = 0;
        for(Repository repo : getMax.keySet())
        {
            if(getMax.get(repo).size() > max)
                max = getMax.get(repo).size();
        }

        return max;
    }

    private float getMaxForksForRepo(HashMap<Repository, Integer> getMax) {

        float max = 0;
        for(Repository repo : getMax.keySet())
        {
            if(getMax.get(repo) > max)
                max = getMax.get(repo);
        }

        return max;
    }

    private float getMinForksForRepo(HashMap<Repository, Integer> getMin) {

        float min = 0;
        for(Repository repo : getMin.keySet())
        {
            if(getMin.get(repo) < min)
                min = getMin.get(repo);
        }

        return min;
    }

    private float getMinCommittersForRepo(HashMap<Repository, List<Committer>> getMin) {

        float min = Float.MAX_VALUE;
        for(Repository repo : getMin.keySet())
        {
            if(getMin.get(repo).size() < min)
                min = getMin.get(repo).size();
        }

        return min;
    }

    private float getMinMinForRepo(HashMap<Repository, Integer> getMin) {

        float min = Float.MAX_VALUE;
        for(Repository repo : getMin.keySet())
        {
            if(getMin.get(repo) < min)
                min = getMin.get(repo);
        }

        return min;
    }

    private int getTopCommittersForRepo(Repository repository) {

        int max = 0;
        for(Repository repo : m_adjListCommitters.keySet())
        {
            if(repo.getRepoProject().equals(repository.getRepoProject()))
            {
                if(max < repo.getNumCommitters())
                    max = repo.getNumCommitters();
            }
        }
        return max;
    }

    private int getTopForksForRepo(Repository repository) {

        int max = 0;
        for(Repository repo : m_adjListCommitters.keySet())
        {
            if(repo.getRepoProject().equals(repository.getRepoProject()))
            {
                if(max < repo.getNumOfForks())
                    max = repo.getNumOfForks();
            }
        }
        return max;
    }
}
