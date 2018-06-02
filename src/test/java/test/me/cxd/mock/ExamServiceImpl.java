package test.me.cxd.mock;

import me.cxd.bean.Examination;
import me.cxd.service.ExamService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Profile("test")
@Service
public class ExamServiceImpl implements ExamService {

    @Override
    public Examination find(long id) {
        return null;
    }

    @Override
    public long add(Examination examination, long classroomId) {
        return 0;
    }

    @Override
    public void modify(long id, Examination examination, long classroomId) {
    }

    public void addSupervisor(long id, long number) {
    }

    @Override
    public void removeSupervisor(long id, long number) {
    }

    @Override
    public void remove(long id) {

    }

    @Override
    public long count(Long number, Long classroomId, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException {
        return 0;
    }

    @Override
    public List<Examination> find(long begIndex, long count, Long number, Long classroomId, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException {
        return null;
    }
}
