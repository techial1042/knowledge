package top.techial.knowledge.service;

import org.springframework.stereotype.Service;
import top.techial.knowledge.dao.RecordRepository;
import top.techial.knowledge.domain.Record;

/**
 * @author techial
 */
@Service
public class RecordService {
    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Record save(Record record) {
        return recordRepository.save(record);
    }
}