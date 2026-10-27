package com.knowledge.realm.ai.knowledge;

import com.knowledge.realm.ai.*;
import com.knowledge.realm.mapper.LearningMapper;
import com.knowledge.realm.exception.BusinessException;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

@Service
public class KnowledgeRetrievalService {
    private final AiMapper mapper;
    private final LearningMapper learning;
    public KnowledgeRetrievalService(AiMapper mapper, LearningMapper learning) { this.mapper=mapper; this.learning=learning; }
    public Set<Long> scope(long current) {
        if (learning.knowledge(current)==null) throw BusinessException.notFound();
        Set<Long> ids=new HashSet<>(learning.related(current)); ids.add(current); return Set.copyOf(ids);
    }
    public List<AiContracts.Source> retrieve(long current, String question) {
        Set<Long> ids=scope(current);
        boolean personal=question.matches("(?s).*(错题|进度|复习|学习记录).*" );
        Set<String> terms=terms(personal?learning.knowledge(current).name()+" "+question:question);
        return mapper.reviewedChunks(ids).stream().filter(c -> ids.contains(c.knowledgeId()))
            .map(c -> Map.entry(c, score(terms,c.title()+" "+c.body())))
            .filter(e -> e.getValue()>0).sorted(Comparator.<Map.Entry<AiContracts.Chunk,Integer>>comparingInt(Map.Entry::getValue).reversed().thenComparingLong(e -> e.getKey().id()))
            .limit(4).map(e -> source(e.getKey())).toList();
    }
    public List<AiContracts.Source> approvedFor(long current) {
        Set<Long> ids=scope(current);
        return mapper.reviewedChunks(ids).stream().filter(c -> ids.contains(c.knowledgeId())).limit(8).map(this::source).toList();
    }
    private AiContracts.Source source(AiContracts.Chunk c) {
        return new AiContracts.Source("K"+c.id(),c.knowledgeId(),c.sourceTitle()+" · "+c.title(),c.body(),c.sourceUrl(),c.sourceVersion());
    }
    // Character bigrams support Chinese without an external tokenizer or embedding service.
    // Professional fragments are small and independently audited; no story text is queried.
    public static Set<String> terms(String text) {
        Set<String> result=new HashSet<>();
        String normalized=text.toLowerCase(Locale.ROOT);
        for (String word:normalized.split("[^\\p{IsHan}a-z0-9]+")) {
            if (word.matches("[a-z0-9]+")) { if (word.length()>1) result.add(word); }
            else for(int i=0;i<word.length()-1;i++) {
                String term=word.substring(i,i+2);
                if (!Set.of("为什么","什么","如何","怎么","一下","一个","这个","可以","不能","的是").contains(term)) result.add(term);
            }
        }
        return result;
    }
    private int score(Set<String> terms,String text) {
        String lower=text.toLowerCase(Locale.ROOT);
        return (int)terms.stream().filter(lower::contains).count();
    }
}
