package me.cxd.service;

import me.cxd.bean.Annex;
import me.cxd.bean.AnnexType;
import me.cxd.bean.Reply;
import me.cxd.bean.Task;
import org.springframework.beans.TypeMismatchException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface TaskService {
    enum Order {
        DEADLINE("deadline"), INSERT_TIME("insertTime");

        private final String val;

        Order(String val) {
            this.val = val;
        }

        public String value() {
            return val;
        }
    }

    void addAnnexType(AnnexType... types);

    void add(Reply reply, long taskId, long userId);

    @Transactional
    default void add(Reply reply, long taskId, long userId, Path base, byte[] bytes, String fileName) throws IOException {
        add(reply, taskId, userId);
        storeReplyAnnex(reply.getId(), base, bytes, fileName);
    }

    /**
     * Find replies which belong to the task with the given ID.
     *
     * @param taskId:   task's ID
     * @param begIndex: begin index
     * @param count:    retrieve count
     * @return replies
     */
    List<Reply> findReplies(long taskId, int begIndex, int count);

    long countReplies(long taskId);

    /**
     * @return Find all supported file types.
     */
    List<String> findSupportedAnnexTypes();

    Annex passToFill(long taskId, long userId, byte[] bytes) throws IOException;

    /**
     * Store the uploaded file, and return the annex object.
     *
     * @param replyId: the reply which the annex belongs to
     * @param base:    base directory
     * @param bytes:   file's bytes array
     * @return newly created annex
     */
    Annex storeReplyAnnex(long replyId, Path base, byte[] bytes, String fileName) throws IOException;

    /**
     * Store the uploaded file, and return the annex object.
     *
     * @param taskId: the task which the annex belongs to
     * @param base:   base directory
     * @param bytes:  file's bytes array
     * @return newly created annex
     */
    Annex storeTaskAnnex(long taskId, Path base, byte[] bytes, String fileName) throws IOException;

    /**
     * Retrieve the annex with the given check sum.
     *
     * @param userId: the ID of the user who did this action
     * @param id:     the ID of annex
     * @return found annex
     */
    byte[] retrieve(long userId, long id, String[] fileName) throws IOException;

    /**
     * Find task with the given ID.
     *
     * @param id: task's ID
     * @return task
     */
    Task find(long id);

    List<Task> findNotReplied(Boolean requiredAnnex, Boolean passToFill, long teacherId, int beginIndex, int count, Order order);

    List<Task> findAllReplied(Boolean requiredAnnex, Boolean passToFill, int beginIndex, int count, Order order);

    List<Task> find(Boolean requiredAnnex, Boolean passToFill, int beginIndex, int count, Order order);

    long countNotReplied(Boolean requiredAnnex, Boolean passToFill, long teacherId);

    long countAllReplied(Boolean requiredAnnex, Boolean passToFill);

    long count(Boolean requiredAnnex, Boolean passToFill);

    /**
     * Add a new task whose required annex type has the given ID.
     *
     * @param task: task to be added
     */
    void add(Task task, long userId);

    @Transactional
    default Annex add(Task task, long userId, Path base, byte[] bytes, String fileName) throws IOException {
        add(task, userId);
        return storeTaskAnnex(task.getId(), base, bytes, fileName);
    }
}
