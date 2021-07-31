public class Edge {

    private Committer committer;
    private Repository repo;
    private long numberOfRepos;

    Edge(Committer committer, Repository repo)
    {
        this.committer = committer;
        this.repo = repo;
    }

    Edge(Committer committer, Repository repo, long numberOfRepos)
    {
        this.committer = committer;
        this.repo = repo;
        this.numberOfRepos = numberOfRepos;
    }

    public Committer getCommitter() {
        return committer;
    }

    public Repository getRepo() {
        return repo;
    }

    public long getNumberOfRepos() {
        return numberOfRepos;
    }
}
