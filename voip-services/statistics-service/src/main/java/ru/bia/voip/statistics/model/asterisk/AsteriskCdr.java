package ru.bia.voip.statistics.model.asterisk;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Data
@NoArgsConstructor
@Table(name = "cdr")
@NamedQueries(
        {
                @NamedQuery(
                        name = "mapExtensionToNumberOfIncomingCallsByDate",
                        query = "select dst as dstAlias, count(dst) as dstCount " +
                                "from AsteriskCdr " +
                                "where dst in (:dstList) " +
                                "and calldate between :from  and :to group by dst"
                ),
                @NamedQuery(
                        name = "mapExtensionToNumberOfOutgoingCallsByDate",
                        query = "select src as srcAlias, count(src) as srcCount " +
                                "from AsteriskCdr " +
                                "where src in (:srcList)  " +
                                "and calldate between :from  and :to group by src"

                )
        }
)
public class AsteriskCdr {
    @Id
    private Timestamp calldate;
    private String clid;
    private String src;
    private String dst;
    private Integer duration;
    private String disposition;
}
