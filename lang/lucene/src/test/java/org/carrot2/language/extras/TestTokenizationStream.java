/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.extras;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.icu.segmentation.DefaultICUTokenizerConfig;
import org.apache.lucene.analysis.icu.segmentation.ICUTokenizer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.util.AttributeFactory;
import org.carrot2.TestBase;
import org.carrot2.language.Tokenizer;
import org.carrot2.util.MutableCharArray;
import org.carrot2.util.TabularOutput;
import org.junit.Test;

public class TestTokenizationStream extends TestBase {
  @Test
  public void tokenizeWithAdapter() throws IOException {
    LuceneAnalyzerTokenizerAdapter tokenizer = new LuceneAnalyzerTokenizerAdapter(new KoreanAnalyzer());
    tokenizer.reset(new StringReader("悠久한 歷史와 傳統에 빛나는 우리 大韓國民은 3·1 運動으로 建立된 大韓民國臨時政府의 法統과 不義에 抗拒한 4·19 民主理念을 繼承하고, 祖國의 民主改革과 平和的統一의 使命에 立脚하여 正義·人道와 同胞愛로써 民族의 團結을 鞏固히 하고, 모든 社會的弊習과 不義를 打破하며, 自律과 調和를 바탕으로 自由民主的基本秩序를 더욱 確固히 하여 政治·經濟·社會·文化의 모든 領域에 있어서 各人의 機會를 均等히 하고, 能力을 最高度로 發揮하게 하며, 自由와 權利에 따르는 責任과 義務를 完遂하게 하여, 안으로는 國民生活의 均等한 向上을 基하고 밖으로는 恒久的인 世界平和와 人類共榮에 이바지함으로써 우리들과 우리들의 子孫의 安全과 自由와 幸福을 永遠히 確保할 것을 다짐하면서 1948年 7月 12日에 制定되고 8次에 걸쳐 改正된 憲法을 이제 國會의 議決을 거쳐 國民投票에 依하여 改正한다.\n"
        + "\n"
        + "1987年 10月 29日"));

    TabularOutput t =
        TabularOutput.to(new StringWriter())
            .noAutoFlush()
            .addColumn("type", c -> c.alignRight())
            .addColumn("image", c -> c.alignLeft())
            .build();

    short token;
    MutableCharArray image = new MutableCharArray();
    while ((token = tokenizer.nextToken()) != Tokenizer.TT_EOF) {
      t.append(token);
      tokenizer.setTermBuffer(image);
      t.append(image.toString());
      t.nextRow();
    }

    System.out.println(t.flush().getWriter().toString());
  }

  @Test
  public void tokenizeEnglish() throws IOException {
    Analyzer analyzer = new EnglishAnalyzer();
    System.out.println(
        TokenStreams.dumpAttributes(
            "Dummy foo bar. Where is he?", "", analyzer, new StringWriter()));
  }

  @Test
  public void tokenizeKoreanHangul() throws IOException {
    Analyzer analyzer = new KoreanAnalyzer();
    System.out.println(
        TokenStreams.dumpAttributes(
            "유구한 역사와 전통에 빛나는 우리 대한 국민은 3·1 운동으로 건립된 대한민국 임시 정부의 법통과 불의에 항거한 4·19 민주 이념을 계승하고, 조국의 민주 개혁과 평화적 통일의 사명에 입각하여 정의·인도와 동포애로써 민족의 단결을 공고히 하고, 모든 사회적 폐습과 불의를 타파하며, 자율과 조화를 바탕으로 자유 민주적 기본 질서를 더욱 확고히 하여 정치·경제·사회·문화의 모든 영역에 있어서 각인의 기회를 균등히 하고, 능력을 최고도로 발휘하게 하며, 자유와 권리에 따르는 책임과 의무를 완수하게 하여, 안으로는 국민 생활의 균등한 향상을 기하고 밖으로는 항구적인 세계 평화와 인류 공영에 이바지함으로써 우리들과 우리들의 자손의 안전과 자유와 행복을 영원히 확보할 것을 다짐하면서 1948년 7월 12일에 제정되고 8차에 걸쳐 개정된 헌법을 이제 국회의 의결을 거쳐 국민 투표에 의하여 개정한다.\n"
                + "1987년 10월 29일",
            "",
            analyzer,
            new StringWriter()));
  }

  @Test
  public void tokenizeKoreanMixedScript() throws IOException {
    Analyzer analyzer = new KoreanAnalyzer();
    System.out.println(
        TokenStreams.dumpAttributes(
            "悠久한 歷史와 傳統에 빛나는 우리 大韓國民은 3·1 運動으로 建立된 大韓民國臨時政府의 法統과 不義에 抗拒한 4·19 民主理念을 繼承하고, 祖國의 民主改革과 平和的統一의 使命에 立脚하여 正義·人道와 同胞愛로써 民族의 團結을 鞏固히 하고, 모든 社會的弊習과 不義를 打破하며, 自律과 調和를 바탕으로 自由民主的基本秩序를 더욱 確固히 하여 政治·經濟·社會·文化의 모든 領域에 있어서 各人의 機會를 均等히 하고, 能力을 最高度로 發揮하게 하며, 自由와 權利에 따르는 責任과 義務를 完遂하게 하여, 안으로는 國民生活의 均等한 向上을 基하고 밖으로는 恒久的인 世界平和와 人類共榮에 이바지함으로써 우리들과 우리들의 子孫의 安全과 自由와 幸福을 永遠히 確保할 것을 다짐하면서 1948年 7月 12日에 制定되고 8次에 걸쳐 改正된 憲法을 이제 國會의 議決을 거쳐 國民投票에 依하여 改正한다.\n"
                + "\n"
                + "1987年 10月 29日",
            "",
            analyzer,
            new StringWriter()));
  }

  @Test
  public void tokenizeJapanese() throws IOException {
    Analyzer analyzer = new JapaneseAnalyzer();
    System.out.println(
        TokenStreams.dumpAttributes(
            "横須賀海軍施設ドックは、神奈川県横須賀市の在日アメリカ海軍横須賀海軍施設内にあり、米海軍ならびに海上自衛隊の艦艇修理に使用されている6基のドライドックである。最古の1号ドックは横須賀造船所時代の明治4年（1871年）に完成しているが、現在もなお使用されている。最大の6号ドックは大和型戦艦の建造ならびに修理・改造を行うことを目的とし、昭和15年（1940年）に完成したドックであり、現在は米海軍空母の修理などに使用されている",
            "",
            analyzer,
            new StringWriter()));
  }

  @Test
  public void tokenizeTraditionalChinese() throws IOException {
    Analyzer analyzer = new ICUAnalyzer();
    System.out.println(TokenStreams.dumpAttributes("我購買了道具和服裝。", "", analyzer, new StringWriter()));
  }

  private static class ICUAnalyzer extends Analyzer {
    protected TokenStreamComponents createComponents(String fieldName) {
      return new TokenStreamComponents(
          new ICUTokenizer(
              AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
              new DefaultICUTokenizerConfig(true, true)));
    }
  }
}
