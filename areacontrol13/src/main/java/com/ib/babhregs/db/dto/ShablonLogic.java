package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Логика за попълване на шаблон
 */
@Entity
@Table(name = "shablon_logic")
public class ShablonLogic extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = 6616772917958272580L;

	public static final String DUBLIKAT_BM_LABEL = "dublikat";

	@SequenceGenerator(name = "ShablonLogic", sequenceName = "seq_shablon_logic", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "ShablonLogic")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "file_id")
	@JournalAttr(label = "file_id", defaultText = "ИД на файл (темплейт)")
	private Integer fileId; // (int8)

	@JournalAttr(label = "bookmarks", defaultText = "Bookmark в шаблон")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "shablon_logic_id", referencedColumnName = "id", nullable = false)
	private List<ShablonBookmark> bookmarks;

	/** */
	public ShablonLogic() {
		super();
	}

	/** @return the bookmarks */
	public List<ShablonBookmark> getBookmarks() {
		return this.bookmarks;
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_SHABLON_LOGIC;
	}

	/** @return the fileId */
	public Integer getFileId() {
		return this.fileId;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return null;
	}

	/** @param bookmarks the bookmarks to set */
	public void setBookmarks(List<ShablonBookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	/** @param fileId the fileId to set */
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		return journal;
	}
}
