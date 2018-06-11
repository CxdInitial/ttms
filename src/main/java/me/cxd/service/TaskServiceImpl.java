package me.cxd.service;

import me.cxd.bean.*;
import me.cxd.dao.JpaDao;
import me.cxd.util.FileUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final UserService userService;
    private final JpaDao<Task> taskDao;
    private final JpaDao<Annex> annexDao;
    private final JpaDao<Reply> replyDao;
    private final JpaDao<AnnexType> typeDao;
    private final JpaDao<DownloadRecord> recordDao;
    private final FileUtils fileUtils;

    @Autowired
    public TaskServiceImpl(UserService userService, JpaDao<Task> taskDao, JpaDao<Annex> annexDao, JpaDao<Reply> replyDao, JpaDao<AnnexType> typeDao, JpaDao<DownloadRecord> recordDao, FileUtils fileUtils) {
        this.userService = userService;
        this.taskDao = taskDao;
        this.annexDao = annexDao;
        this.replyDao = replyDao;
        this.typeDao = typeDao;
        this.recordDao = recordDao;
        this.fileUtils = fileUtils;
    }

    @Override
    public void addAnnexType(AnnexType... types) {
        Arrays.stream(types).forEach(typeDao::create);
    }

    @Override
    public void add(Reply reply, long taskId, long userId) {
        Teacher replier = userService.find(userId);
        if (replier == null)
            throw new NoSuchElementException();
        reply.setReplier(replier);
        Task task = taskDao.read(taskId);
        if (task == null)
            throw new NoSuchElementException();
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Reply> root = query.from(Reply.class);
        query.select(builder.count(root.get("id")));
        query.where(builder.equal(root.get("replier").get("id"), userId), builder.equal(root.get("task").get("id"), taskId));
        if (replyDao.getEntityManager().createQuery(query).getSingleResult() != 0)
            throw new IllegalArgumentException("Already replied!");
        reply.setTask(task);
        replyDao.create(reply);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Reply> findReplies(long taskId, int begIndex, int count) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Reply> query = builder.createQuery(Reply.class);
        Root<Reply> root = query.from(Reply.class);
        query.select(root);
        query.orderBy(builder.asc(root.get("insertTime")));
        query.where(builder.equal(root.get("task").get("id"), taskId));
        return replyDao.getEntityManager().createQuery(query).setFirstResult(begIndex).setMaxResults(count).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long countReplies(long taskId) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Reply> root = query.from(Reply.class);
        query.select(builder.count(root.get("id")));
        query.where(builder.equal(root.get("task").get("id"), taskId));
        return replyDao.getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<String> findSupportedAnnexTypes() {
        CriteriaBuilder builder = typeDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        query.select(query.from(AnnexType.class).get("suffix"));
        query.distinct(true);
        return typeDao.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public Annex passToFill(long taskId, long userId, byte[] bytes) throws IOException {
        Teacher user = userService.find(userId);
        if (user == null)
            throw new NoSuchElementException("Found no user with the given ID.");
        Task task = taskDao.read(taskId);
        if (task == null)
            throw new NoSuchElementException("Found no task with the given ID.");
        Annex annex = task.getAnnex();
        if (!task.getPassToFill() || annex == null)
            throw new IllegalArgumentException();
        CriteriaBuilder builder = recordDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<DownloadRecord> query = builder.createQuery(DownloadRecord.class);
        Root<DownloadRecord> root = query.from(DownloadRecord.class);
        query.select(root);
        query.where(builder.equal(root.get("annex").get("id"), annex.getId()), builder.equal(root.get("user").get("id"), userId));
        query.orderBy(builder.desc(root.get("actionTime")));
        DownloadRecord record;
        try {
            record = recordDao.getEntityManager().createQuery(query).setMaxResults(1).getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
        if (annex.getCheckSum().equalsIgnoreCase(record.getCheckSum()) && record.getByteSize() <= bytes.length && (!task.getStrictMode() || task.getStrictMode() && fileUtils.hasValidSignature(bytes, annex.getFileType()))) {
            annex.setPath(Files.write(Paths.get(annex.getPath()), bytes).toString());
            annex.setCheckSum(fileUtils.md5(bytes));
            return annex;
        }
        throw new IllegalArgumentException();
    }

    private AnnexType getType(byte[] bytes, String suffix) {
        CriteriaBuilder builder = typeDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<AnnexType> query = builder.createQuery(AnnexType.class);
        Root<AnnexType> root = query.from(AnnexType.class);
        query.select(root);
        query.where(builder.equal(root.get("suffix"), suffix));
        return typeDao.getEntityManager().createQuery(query).getResultList().stream().filter(type -> fileUtils.hasValidSignature(bytes, type)).findFirst().orElse(null);
    }

    @Override
    public Annex storeReplyAnnex(long replyId, Path base, byte[] bytes, String fileName) throws TypeMismatchException, IOException {
        Reply reply = replyDao.read(replyId);
        if (reply == null)
            throw new NoSuchElementException();
        Task task = reply.getTask();
        if (task.getRequiredAnnexType() == null)
            throw new IllegalArgumentException();
        String[] names = fileName.split("\\.");
        String suffix = names[names.length - 1].toLowerCase();
        if (!suffix.equals(task.getRequiredAnnexType()))
            throw new TypeMismatchException((Object) null, null);
        Annex annex = new Annex();
        if (task.getStrictMode())
            annex.setFileType(getType(bytes, suffix));
        if (annex.getFileType() == null)
            throw new TypeMismatchException((Object) null, null);
        annex.setPath(Files.write(base.resolve(String.format("%d-%d.%s", task.getId(), replyId, suffix)), bytes).toString());
        annex.setCheckSum(fileUtils.md5(bytes));
        annex.setReply(reply);
        annexDao.create(annex);
        return annex;
    }

    @Override
    public Annex storeTaskAnnex(long taskId, Path base, byte[] bytes, String fileName) throws TypeMismatchException, IOException {
        Task task = taskDao.read(taskId);
        if (task == null)
            throw new NoSuchElementException();
        if (task.getAnnex() != null)
            throw new IllegalArgumentException();
        Annex annex = new Annex();
        annex.setCheckSum(fileUtils.md5(bytes));
        String[] names = fileName.split("\\.");
        String suffix = names[names.length - 1].toLowerCase();
        Annex old = findAnnex(annex.getCheckSum());
        if (old != null && !task.getPassToFill())
            annex.setPath(old.getPath());
        else
            annex.setPath(Files.write(base.resolve(String.format("%d.%s", taskId, suffix)), bytes).toString());
        if (task.getPassToFill()) {
            if (!suffix.equals(task.getRequiredAnnexType()))
                throw new TypeMismatchException((Object) null, null);
            if (task.getStrictMode())
                annex.setFileType(getType(bytes, suffix));
        }
        if (annex.getFileType() == null)
            throw new TypeMismatchException((Object) null, null);
        annexDao.create(annex);
        task.setAnnex(annex);
        return annex;
    }

    @Override
    public byte[] retrieve(long userId, long id, String[] fileName) throws IOException {
        Annex annex = annexDao.read(id);
        if (annex == null)
            throw new NoSuchElementException("Found no list with the given check sum.");
        Teacher user = userService.find(userId);
        if (user == null)
            throw new NoSuchElementException("Found no user with the given ID.");
        Reply reply = annex.getReply();
        if (reply != null)
            fileName[0] = String.format("%s-%d.%s", reply.getTask().getTitle(), reply.getReplier().getTeacherNo(), reply.getTask().getRequiredAnnexType());
        else {
            CriteriaBuilder builder = taskDao.getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Task> query = builder.createQuery(Task.class);
            Root<Task> root = query.from(Task.class);
            query.select(root);
            query.where(builder.equal(root.get("annex").get("id"), id));
            List<Task> list = taskDao.getEntityManager().createQuery(query).getResultList();
            if (list.isEmpty())
                throw new IllegalArgumentException();
            fileName[0] = String.format("%s.%s", list.get(0).getTitle(), list.get(0).getRequiredAnnexType());
            DownloadRecord record = new DownloadRecord();
            record.setUser(user);
            record.setAnnex(annex);
            record.setCheckSum(annex.getCheckSum());
            recordDao.create(record);
        }
        return Files.readAllBytes(Paths.get(annex.getPath()));
    }

    private Annex findAnnex(String checkSum) {
        CriteriaBuilder builder = annexDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Annex> query = builder.createQuery(Annex.class);
        Root<Annex> root = query.from(Annex.class);
        query.select(root);
        query.where(builder.equal(root.get("checkSum"), checkSum));
        List<Annex> list = annexDao.getEntityManager().createQuery(query).getResultList();
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Task find(long id) {
        return taskDao.read(id);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Task> findNotReplied(Boolean requiredAnnex, Boolean passToFill, long teacherId, int beginIndex, int count, Order order) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>(3);
        predicates.add(builder.lt(builder.size(root.get("replies")), userService.countUser()));
        resolveTaskType(requiredAnnex, passToFill, builder, root, predicates);
        query.where(predicates.toArray(new Predicate[0]));
        return taskDao.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Task> findAllReplied(Boolean requiredAnnex, Boolean passToFill, int beginIndex, int count, Order order) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>(3);
        predicates.add(builder.equal(builder.size(root.get("replies")), userService.countUser()));
        resolveTaskType(requiredAnnex, passToFill, builder, root, predicates);
        query.where(predicates.toArray(new Predicate[0]));
        return replyDao.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Task> find(Boolean requiredAnnex, Boolean passToFill, int beginIndex, int count, Order order) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>(2);
        resolveTaskType(requiredAnnex, passToFill, builder, root, predicates);
        query.where(predicates.toArray(new Predicate[0]));
        return replyDao.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long countNotReplied(Boolean requiredAnnex, Boolean passToFill, long teacherId) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Task> root = query.from(Task.class);
        query.select(builder.count(root.get("id")));
        List<Predicate> predicates = new ArrayList<>(3);
        predicates.add(builder.lt(builder.size(root.get("replies")), userService.countUser()));
        resolveTaskType(requiredAnnex, passToFill, builder, root, predicates);
        query.where(predicates.toArray(new Predicate[0]));
        return replyDao.getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long countAllReplied(Boolean requiredAnnex, Boolean passToFill) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Task> root = query.from(Task.class);
        query.select(builder.count(root.get("id")));
        List<Predicate> predicates = new ArrayList<>(3);
        predicates.add(builder.equal(builder.size(root.get("replies")), userService.countUser()));
        resolveTaskType(requiredAnnex, passToFill, builder, root, predicates);
        query.where(predicates.toArray(new Predicate[0]));
        return replyDao.getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long count(Boolean requiredAnnex, Boolean passToFill) {
        CriteriaBuilder builder = replyDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Task> root = query.from(Task.class);
        query.select(builder.count(root.get("id")));
        List<Predicate> predicates = new ArrayList<>(2);
        resolveTaskType(requiredAnnex, passToFill, builder, root, predicates);
        query.where(predicates.toArray(new Predicate[0]));
        return replyDao.getEntityManager().createQuery(query).getSingleResult();
    }

    private void resolveTaskType(Boolean requiredAnnex, Boolean passToFill, CriteriaBuilder builder, Root<Task> root, List<Predicate> predicates) {
        if (requiredAnnex != null)
            if (!requiredAnnex)
                predicates.add(builder.isNull(root.get("requiredAnnexType")));
            else if (passToFill != null)
                predicates.add(builder.equal(root.get("passToFill"), passToFill));
    }

    @Override
    public void add(Task task, long userId) {
        Teacher user = userService.find(userId);
        if (user == null)
            throw new NoSuchElementException();
        task.setSubmitter(user);
        taskDao.getEntityManager().persist(task);
    }
}
