import java.util.Objects;

public class Repository {

    private String repoId;
    private String repoLink;
    private Integer numCommitters;
    private boolean isIndependent;
    private Integer numOfForks;
    private  String repoProject;
    private float norm;

    Repository(String repoId, String repoLink, Integer numCommitters, boolean isIndependent, Integer numOfForks)
    {
        this.repoId = repoId;
        this.repoLink = repoLink;
        this.numCommitters = numCommitters;
        this.isIndependent = isIndependent;
        this.numOfForks = numOfForks;
        repoProject = repoLink.substring(repoLink.lastIndexOf("/") + 1);
    }

    public Integer getNumCommitters() {
        return numCommitters;
    }

    public float getNorm() {
        return norm;
    }

    public String getRepoLink() {
        return repoLink;
    }

    public Integer getNumOfForks() {
        return numOfForks;
    }

    public String getRepoProject() {
        return repoProject;
    }

    public void setNorm(float norm) {
        this.norm = norm;
    }

    public void setNumCommitters(Integer numCommitters) {
        this.numCommitters = numCommitters;
    }

    public void setNumOfForks(Integer numOfForks) {
        this.numOfForks = numOfForks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return Objects.equals(repoId, that.repoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repoId);
    }

    @Override
    public String toString() {
        return  "Repo -> " + repoProject +
                " | Committers -> " + numCommitters +
                " | Forks -> " + numOfForks +
                " | Committers norm: " + norm;
    }
}
